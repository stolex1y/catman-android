package ru.stolexiy.common

import androidx.annotation.AnyThread
import androidx.annotation.GuardedBy
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.stolexiy.common.TimeConstants.MIN_TO_MS
import ru.stolexiy.common.TimeConstants.MIN_TO_SEC
import ru.stolexiy.common.TimeConstants.SEC_TO_MS
import ru.stolexiy.common.di.CoroutineDispatcherNames
import java.util.PriorityQueue
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.max
import kotlin.math.min

open class Timer @Inject constructor(
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) coroutineDispatcher: CoroutineDispatcher
) {
    companion object {
        private const val TAG = "[AY] Timer"

        @JvmStatic
        private val DEFAULT_TIME_MAX = Time((999L * MIN_TO_SEC + 59L) * SEC_TO_MS)
    }

    protected val mutex = Mutex()

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

    @GuardedBy("mutex")
    private val _curTime = Time(initTime)
    val curTime: ImmutableTime
        get() = _curTime

    var state: State = State.STOPPED
        @GuardedBy("mutex") private set

    private val coroutineScope = CoroutineScope(
        CoroutineName(TAG) +
                coroutineDispatcher +
                CoroutineExceptionHandler { _, throwable -> errors = throwable }
    )

    @VisibleForTesting
    internal var errors: Throwable? = null
        private set

    @VisibleForTesting
    @GuardedBy("mutex")
    private var timerJob: Job? = null

    @AnyThread
    fun start() {
        coroutineScope.launch {
            mutex.withLock {
                timerJob = when (state) {
                    State.RUNNING -> return@launch
                    State.STOPPED -> {
                        _curTime.setTime(initTime)
                        resetListenersUpdateTime()
                        timerRun()
                    }

                    State.PAUSED -> timerRun()
                }
            }
        }
    }

    @AnyThread
    fun pause() {
        coroutineScope.launch {
            mutex.withLock {
                if (state == State.RUNNING) {
                    cancelTimerJob()
                    onPause()
                }
            }
        }
    }

    @AnyThread
    fun stop() {
        coroutineScope.launch {
            mutex.withLock {
                if (state == State.STOPPED)
                    return@launch
                cancelTimerJob()
                onStop()
            }
        }
    }

    @AnyThread
    fun reset() {
        coroutineScope.launch {
            mutex.withLock {
                cancelTimerJob()
                onStop()
            }
        }
    }

    @AnyThread
    fun addListener(listener: Listener) {
        listenersLock.lock()
        if (!listeners.contains(listener)) {
            listeners.add(listener)
            if (listener.updateTime > 0L)
                listenersNextUpdateTime.add(listener.updateTime to listener)
        }
        listenersLock.unlock()
    }

    @AnyThread
    fun removeListener(listener: Listener) {
        listenersLock.lock()
        if (listeners.contains(listener)) {
            listeners.remove(listener)
            listenersNextUpdateTime.removeIf { it.second == listener }
        }
        listenersLock.unlock()
    }

    @AnyThread
    @GuardedBy("mutex")
    protected fun onPause() {
        state = State.PAUSED
        listeners.forEach { it.onPause(this) }
    }

    @AnyThread
    @GuardedBy("mutex")
    protected fun onStart() {
        state = State.RUNNING
        listeners.forEach { it.onStart(this) }
    }

    @AnyThread
    @GuardedBy("mutex")
    protected fun onStop() {
        _curTime.setTime(initTime)
        resetListenersUpdateTime()
        state = State.STOPPED
        listeners.forEach { it.onStop(this) }
    }

    @AnyThread
    @GuardedBy("mutex")
    protected fun onFinish() {
        state = State.STOPPED
        listeners.forEach { it.onFinish(this) }
    }

    @AnyThread
    @GuardedBy("mutex")
    private suspend fun cancelTimerJob() {
        timerJob = timerJob?.let {
            it.cancelAndJoin()
            null
        }
    }

    @AnyThread
    @GuardedBy("mutex")
    private fun timerRun(): Job {
        return coroutineScope.launch {
            onStart()
            while (_curTime > 0) {
                val nextListenerUpdateTime = listenersNextUpdateTime.firstOrNull()?.first ?: 0
                val delayTime = curTime.inMs - nextListenerUpdateTime
                delay(delayTime)
                _curTime.minus(delayTime)
                onUpdateCurTime()
            }
            onFinish()
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

    abstract class ImmutableTime {
        abstract val inMs: Long
        abstract val min: Long
        abstract val sec: Long
        abstract val ms: Long

        operator fun compareTo(ms: Long): Int {
            return this.inMs.compareTo(ms)
        }

        operator fun compareTo(other: ImmutableTime): Int {
            return this.inMs.compareTo(other.inMs)
        }

        override operator fun equals(other: Any?): Boolean {
            if (other == null)
                return false
            return when (other) {
                is Long -> this.inMs == other
                is ImmutableTime -> this.inMs == other.inMs
                else -> false
            }
        }

        override fun hashCode(): Int {
            return inMs.hashCode()
        }
    }

    class Time(ms: Long) : ImmutableTime() {
        private var _inMs: Long = 0
        private var _min: Long = 0
        private var _sec: Long = 0
        private var _ms: Long = 0

        override val inMs: Long
            get() = _inMs
        override val min: Long
            get() = _min
        override val sec: Long
            get() = _sec
        override val ms: Long
            get() = _ms

        init {
            setTime(ms)
        }

        constructor(min: Long, sec: Long) : this(minAndSecToMs(min, sec))

        constructor(time: ImmutableTime) : this(time.inMs)

        operator fun minus(ms: Long) {
            setTime(max(this.inMs - ms, 0))
        }

        operator fun minus(time: ImmutableTime) {
            setTime(max(this.inMs - time.inMs, 0))
        }

        fun setTime(inMs: Long) {
            this._inMs = inMs
            _min = inMs / MIN_TO_MS
            _sec = inMs % MIN_TO_MS / SEC_TO_MS
            _ms = inMs % SEC_TO_MS
        }

        fun setTime(time: ImmutableTime) {
            setTime(time.inMs)
        }

        private companion object {
            @JvmStatic
            private fun minAndSecToMs(min: Long, sec: Long): Long {
                return (min * MIN_TO_SEC + sec) * SEC_TO_MS
            }
        }

        override fun toString(): String {
            return "$_min min, $_sec sec ($_inMs ms)"
        }
    }

    enum class State {
        STOPPED,
        RUNNING,
        PAUSED
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
    }
}
