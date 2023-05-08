package ru.stolexiy.common

import androidx.annotation.AnyThread
import androidx.annotation.GuardedBy
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.stolexiy.common.TimeConstants.MIN_TO_MS
import ru.stolexiy.common.TimeConstants.MIN_TO_SEC
import ru.stolexiy.common.TimeConstants.SEC_TO_MS
import kotlin.math.max
import kotlin.math.min

open class Timer(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    initTime: Time = Time(0),
    updateTime: Time = Time(1 * SEC_TO_MS),
) {
    companion object {
        private const val TAG = "AY: Timer"

        @JvmStatic
        private val DEFAULT_TIME_MAX = Time((999L * MIN_TO_SEC + 59L) * SEC_TO_MS)
    }

    protected val mutex = Mutex()

    @Volatile
    var maxInitTime: Time = DEFAULT_TIME_MAX

    @Volatile
    var listener: TimerListener? = null

    @Volatile
    var initTime: Time = initTime
        set(value) {
            field = Time(max(value.inMs, maxInitTime.inMs))
        }

    var curTime = Time(initTime)
        @GuardedBy("mutex") private set

    private val _curTimeFlow: MutableStateFlow<Time> = MutableStateFlow(initTime)
    val curTimeFlow: StateFlow<Time> = _curTimeFlow.asStateFlow()

    @Volatile
    var updateTime = Time(updateTime)
        set(value) {
            require(value > 0)
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

    @VisibleForTesting
    @GuardedBy("mutex")
    private var timerJob: Job? = null

    fun start() {
        coroutineScope.launch {
            mutex.withLock {
                timerJob = when (state) {
                    State.RUNNING -> return@launch
                    State.STOPPED -> {
                        updateCurTime(initTime.inMs)
                        timerRun()
                    }

                    State.PAUSED -> timerRun()
                }
            }
        }
    }

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

    fun reset() {
        coroutineScope.launch {
            mutex.withLock {
                cancelTimerJob()
                onStop()
            }
        }
    }

    @GuardedBy("mutex")
    protected fun onPause() {
        state = State.PAUSED
        listener?.onPause(this)
    }

    @GuardedBy("mutex")
    protected fun onStart() {
        state = State.RUNNING
        listener?.onStart(this)
    }

    @GuardedBy("mutex")
    protected fun onStop() {
        updateCurTime(initTime.inMs)
        state = State.STOPPED
        listener?.onStop(this)
    }

    @GuardedBy("mutex")
    protected fun onFinish() {
        state = State.STOPPED
        listener?.onFinish(this)
    }

    @GuardedBy("mutex")
    private suspend fun cancelTimerJob() {
        timerJob = timerJob?.let {
            it.cancelAndJoin()
            null
        }
    }

    @GuardedBy("mutex")
    private fun timerRun(): Job {
        return coroutineScope.launch {
            onStart()
            while (curTime > 0) {
                val delayTime = min(updateTime.inMs, curTime.inMs)
                delay(delayTime)
                updateCurTime(curTime - delayTime)
            }
            onFinish()
        }
    }

    @GuardedBy("mutex")
    private fun updateCurTime(ms: Long) {
        curTime = Time(ms)
        _curTimeFlow.value = curTime
        listener?.onUpdateTime(this)
    }

    @GuardedBy("mutex")
    private fun updateCurTime(time: Time) {
        updateCurTime(time.inMs)
    }

    class Time(ms: Long) {
        val inMs: Long
        val min: Long
        val sec: Long

        init {
            inMs = ms
            min = ms / MIN_TO_MS
            sec = ms % MIN_TO_MS / SEC_TO_MS
        }

        constructor(min: Long, sec: Long) : this(minAndSecToMs(min, sec))

        constructor(time: Time) : this(time.inMs)

        operator fun minus(ms: Long) = Time(max(this.inMs - ms, 0))
        operator fun minus(time: Time) = Time(max(this.inMs - time.inMs, 0))

        operator fun compareTo(ms: Long): Int {
            return this.inMs.compareTo(ms)
        }

        private companion object {
            @JvmStatic
            private fun minAndSecToMs(min: Long, sec: Long): Long {
                return (min * MIN_TO_SEC + sec) * SEC_TO_MS
            }
        }

        override fun toString(): String {
            return "$min min, $sec sec ($inMs ms)"
        }
    }

    enum class State {
        STOPPED,
        RUNNING,
        PAUSED
    }

    @AnyThread
    interface TimerListener {
        fun onStart(timer: Timer) {}
        fun onStop(timer: Timer) {}
        fun onPause(timer: Timer) {}
        fun onFinish(timer: Timer) {}
        fun onUpdateTime(timer: Timer) {}
    }
}
