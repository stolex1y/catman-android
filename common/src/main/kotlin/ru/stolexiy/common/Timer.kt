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
import java.util.concurrent.ConcurrentLinkedQueue
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
    var maxInitTime: ITime = DEFAULT_TIME_MAX
        @AnyThread set

    private val listeners = ConcurrentLinkedQueue<Listener>()

    @Volatile
    var initTime: ITime = Time(0)
        @AnyThread
        set(value) {
            field = Time(min(value.inMs, maxInitTime.inMs))
        }

    @GuardedBy("mutex")
    private val _curTime = Time(initTime)
    val curTime: ITime
        get() = _curTime

    @Volatile
    var updateTime: ITime = Time(0, 1)
        @AnyThread set(value) {
            require(value.inMs > 0)
            field = value
        }

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
                        onUpdateCurTime()
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
        if (!listeners.contains(listener))
            listeners.add(listener)
    }

    @AnyThread
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
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
        onUpdateCurTime()
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
                val delayTime = min(updateTime.inMs, curTime.inMs)
                delay(delayTime)
                _curTime.minus(delayTime)
                onUpdateCurTime()
            }
            onFinish()
        }
    }

    @AnyThread
    protected fun onUpdateCurTime() {
        listeners.forEach { it.onUpdateTime(this) }
    }

    interface ITime {
        val inMs: Long
        val min: Long
        val sec: Long
        val ms: Long

        operator fun compareTo(ms: Long): Int {
            return this.inMs.compareTo(ms)
        }

        operator fun compareTo(other: Time): Int {
            return this.inMs.compareTo(other.inMs)
        }
    }

    class Time(ms: Long) : ITime {
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

        constructor(time: ITime) : this(time.inMs)

        operator fun minus(ms: Long) {
            setTime(max(this.inMs - ms, 0))
        }

        operator fun minus(time: ITime) {
            setTime(max(this.inMs - time.inMs, 0))
        }

        fun setTime(inMs: Long) {
            this._inMs = inMs
            _min = inMs / MIN_TO_MS
            _sec = inMs % MIN_TO_MS / SEC_TO_MS
            _ms = inMs % SEC_TO_MS
        }

        fun setTime(time: ITime) {
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
