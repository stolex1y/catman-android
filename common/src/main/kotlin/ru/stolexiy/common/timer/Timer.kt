package ru.stolexiy.common.timer

import androidx.annotation.AnyThread
import androidx.annotation.GuardedBy
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.stolexiy.common.di.SingleThreadDispatcherProvider
import ru.stolexiy.common.timer.TimeConstants.MIN_TO_SEC
import ru.stolexiy.common.timer.TimeConstants.SEC_TO_MS
import java.util.PriorityQueue
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock
import kotlin.math.max
import kotlin.math.min

open class Timer @Inject constructor(
    singleThreadDispatcherProvider: SingleThreadDispatcherProvider
) {
    companion object {
        private const val TAG = "[AY] Timer"

        @JvmStatic
        private val DEFAULT_TIME_MAX = Time((999L * MIN_TO_SEC + 59L) * SEC_TO_MS)
    }

    private val lock: Lock = ReentrantLock()

    @Volatile
    var maxInitTime: ImmutableTime = DEFAULT_TIME_MAX
        @AnyThread set

    @GuardedBy("listenersLock")
    private val listeners: MutableSet<Listener> = mutableSetOf()

    @GuardedBy("listenersLock")
    private val listenersNextUpdateTime: PriorityQueue<Pair<Long, Listener>> =
        PriorityQueue { p1, p2 ->
            -p1.first.compareTo(p2.first)
        }
    private val listenersLock: Lock = ReentrantLock()

    @Volatile
    var initTime: ImmutableTime = Time(0)
        @AnyThread
        set(value) {
            field = Time(min(value.inMs, maxInitTime.inMs))
        }

    @GuardedBy("lock")
    private val _curTime = Time(initTime)
    val curTime: ImmutableTime
        get() = _curTime

    var state: State = State.STOPPED
        @GuardedBy("lock") private set

    private val coroutineDispatcher: CoroutineDispatcher = singleThreadDispatcherProvider.get(TAG)
    private val coroutineScope = CoroutineScope(
        coroutineDispatcher +
                CoroutineExceptionHandler { _, throwable ->
                    System.err.println(throwable.stackTrace)
                    errors = throwable
                }
    )

    @VisibleForTesting
    internal var errors: Throwable? = null
        private set

    @VisibleForTesting
    @GuardedBy("lock")
    private var timerJob: Job? = null

    @AnyThread
    fun start() {
        lock.withLock {
            timerJob = when (state) {
                State.RUNNING -> return@withLock
                State.STOPPED -> {
                    _curTime.setTime(initTime)
                    resetListenersUpdateTime()
                    timerRun()
                }

                State.PAUSED -> timerRun()
                State.DESTROYED -> throw IllegalStateException()
            }
        }
    }

    @AnyThread
    fun pause() {
        lock.withLock {
            if (state == State.RUNNING) {
                cancelTimerJob()
                onPause()
            }
        }
    }

    @AnyThread
    fun stop() {
        lock.withLock {
            if (state == State.STOPPED)
                return@withLock
            cancelTimerJob()
            onStop()
        }
    }

    @AnyThread
    fun reset() {
        lock.withLock {
            cancelTimerJob()
            onStop()
        }
    }

    @AnyThread
    fun addListener(listener: Listener) {
        listenersLock.withLock {
            if (!listeners.contains(listener)) {
                listeners.add(listener)
                if (listener.updateTime > 0L)
                    addListenerNextUpdateTime(listener)
            }
        }
    }

    @AnyThread
    fun removeListener(listener: Listener) {
        listenersLock.withLock {
            if (listeners.contains(listener)) {
                listeners.remove(listener)
                listenersNextUpdateTime.removeIf { it.second == listener }
            }
        }
    }

    @AnyThread
    fun clearListeners() {
        listenersLock.withLock {
            listeners.clear()
            listenersNextUpdateTime.clear()
        }
    }

    @AnyThread
    fun destroy() {
        lock.withLock {
            cancelTimerJob()
            onDestroy()
        }
        coroutineDispatcher.cancel()
    }

    @AnyThread
    @GuardedBy("lock")
    protected fun onPause() {
        state = State.PAUSED
        listeners.forEach { it.onPause(this) }
    }

    @AnyThread
    @GuardedBy("lock")
    protected fun onStart() {
        state = State.RUNNING
        listeners.forEach { it.onStart(this) }
    }

    @AnyThread
    @GuardedBy("lock")
    protected fun onStop() {
        _curTime.setTime(initTime)
        resetListenersUpdateTime()
        state = State.STOPPED
        listeners.forEach { it.onStop(this) }
    }

    @AnyThread
    @GuardedBy("lock")
    protected fun onFinish() {
        state = State.STOPPED
        listeners.forEach { it.onFinish(this) }
    }

    @AnyThread
    @GuardedBy("lock")
    protected fun onDestroy() {
        state = State.DESTROYED
        listeners.forEach { it.onDestroy(this) }
    }

    @AnyThread
    @GuardedBy("lock")
    private fun cancelTimerJob() {
        timerJob = timerJob?.let {
            it.cancel()
            null
        }
    }

    @AnyThread
    private fun timerRun(): Job {
        return coroutineScope.launch {
            lock.withLock { onStart() }
            while (_curTime > 0) {
                val nextListenerUpdateTime = listenersLock.withLock {
                    listenersNextUpdateTime.firstOrNull()?.first ?: 0
                }
                val delayTime = curTime.inMs - nextListenerUpdateTime
                delay(delayTime)
                lock.withLock {
                    _curTime.minus(delayTime)
                    onUpdateCurTime()
                }
            }
            lock.withLock { onFinish() }
        }
    }

    @AnyThread
    protected fun onUpdateCurTime() {
        listenersLock.lock()
        while (listenersNextUpdateTime.isNotEmpty() && listenersNextUpdateTime.peek().first >= curTime.inMs) {
            listenersNextUpdateTime.poll().second.let { listener ->
                listener.onUpdateTime(this)
                addListenerNextUpdateTime(listener)
            }
        }
        listenersLock.unlock()
    }

    private fun resetListenersUpdateTime() {
        listenersLock.lock()
        listeners.forEach { it.onUpdateTime(this) }
        listenersNextUpdateTime.clear()
        listeners.forEach { addListenerNextUpdateTime(it) }
        listenersLock.unlock()
    }

    @GuardedBy("listenersLock")
    private fun addListenerNextUpdateTime(listener: Listener) {
        listenersNextUpdateTime.removeIf { it.second == listener }
        if (listener.updateTime <= 0 || curTime.inMs == 0L)
            return
        listenersNextUpdateTime.add(max(0, curTime.inMs - listener.updateTime) to listener)
    }

    private fun increaseTimerThreadPriority() {
        coroutineScope.launch(coroutineDispatcher) {
            Thread.currentThread().priority = Thread.MAX_PRIORITY
        }
    }

    enum class State {
        STOPPED,
        RUNNING,
        PAUSED,
        DESTROYED
    }

    interface Listener {
        val updateTime: Long
            get() = 0L

        @AnyThread
        fun onStart(timer: Timer) {
        }

        @AnyThread
        fun onStop(timer: Timer) {
        }

        @AnyThread
        fun onPause(timer: Timer) {
        }

        @AnyThread
        fun onFinish(timer: Timer) {
        }

        @AnyThread
        fun onUpdateTime(timer: Timer) {
        }

        @AnyThread
        fun onDestroy(timer: Timer) {
        }
    }
}
