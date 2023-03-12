package ru.stolexiy.catman.core

import androidx.annotation.GuardedBy
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import lombok.EqualsAndHashCode
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

const val SEC_TO_MS = 1000L
const val MIN_TO_SEC = 60L
const val MIN_TO_MS = MIN_TO_SEC * SEC_TO_MS
class Timer(
    coroutineScope: CoroutineScope,
    initTime: Time,
    updateTimeMs: Long = 1 * SEC_TO_MS,
) {
    val DEFAULT_TIME_MAX: Long = (999L * MIN_TO_SEC + 59L) * SEC_TO_MS

    private val mutex = Mutex()

    @Volatile
    var maxInitTime: Long = DEFAULT_TIME_MAX

    @Volatile
    var listener: TimerListener? = null

    @Volatile
    var initTime: Time = initTime
        set(value) {
            field = Time(max(value.ms, maxInitTime))
        }

    private val mCurTimeFlow: MutableStateFlow<Time> = MutableStateFlow(initTime)
    val curTime: StateFlow<Time> = mCurTimeFlow.asStateFlow()

    var updateTimeMs: Long = updateTimeMs
        set(value) {
            require(value > 0)
            field = value
        }

    @GuardedBy("mutex")
    @Volatile
    var state: TimerState = TimerState.STOPPED
        private set

    @Volatile
    var coroutineContext: CoroutineContext = CoroutineName("pomodoro timer") + Dispatchers.Default
        set(value) {
            if (state == TimerState.RUNNING)
                throw IllegalStateException("Couldn't change coroutine context while timer's running")
            field = value
        }

    @Volatile
    var coroutineScope: CoroutineScope = coroutineScope
        set(value) {
            if (state == TimerState.RUNNING)
                throw IllegalStateException("Couldn't change coroutine scope while timer's running")
            field = value
        }

    private var timerJob: Job? = null
    private var mCurTime: Time = initTime

    fun start() {
        coroutineScope.launch(coroutineContext) {
            mutex.withLock {
                when (state) {
                    TimerState.RUNNING -> return@launch
                    TimerState.STOPPED -> {
                        mCurTime = initTime
                        timerJob = timerRun()
                    }
                    TimerState.PAUSED -> timerJob = timerRun()
                }
            }
        }
    }

    fun pause() {
        coroutineScope.launch(coroutineContext) {
            mutex.withLock {
                if (state == TimerState.RUNNING) {
                    timerJob = timerJob?.let {
                        it.cancelAndJoin()
                        null
                    }
                    state = TimerState.PAUSED
                    listener?.onPause()
                }
            }
        }
    }

    fun stop() {
        coroutineScope.launch(coroutineContext) {
            mutex.withLock {
                if (state == TimerState.STOPPED)
                    return@launch
                timerJob = timerJob?.let {
                    it.cancelAndJoin()
                    null
                }
                state = TimerState.STOPPED
                listener?.onStop()
            }
        }
    }

    private fun timerRun(): Job {
        return coroutineScope.launch(coroutineContext) {
            mCurTimeFlow.value = mCurTime
            listener?.onStart()
            state = TimerState.RUNNING
            while (mCurTime > 0) {
                val delayTime = min(updateTimeMs, mCurTime.ms)
                delay(delayTime)
                mCurTime -= delayTime
                mCurTimeFlow.value = mCurTime
            }
            state = TimerState.STOPPED
            listener?.onFinish()
        }
    }

    @EqualsAndHashCode
    class Time(ms: Long) {
        var ms: Long = 0
            private set
        var min: Long = 0
            private set
        var sec: Long = 0
            private set

        init {
            setTime(ms)
        }

        constructor(min: Long, sec: Long) : this(minAndSecToMs(min, sec)) {}

        fun setTime(ms: Long) {
            require(ms >= 0)
            this.ms = ms
            min = ms / MIN_TO_MS
            sec = ms % MIN_TO_MS / SEC_TO_MS
        }

        fun setTime(min: Long, sec: Long) {
            require(min >= 0 && sec >= 0)
            this.min = min
            this.sec = sec
            this.ms = minAndSecToMs(min, sec)
        }

        operator fun minus(ms: Long): Time {
            return Time(max(this.ms - ms, 0))
        }

        operator fun compareTo(ms: Long): Int {
            return this.ms.compareTo(ms)
        }

        private companion object {
            private fun minAndSecToMs(min: Long, sec: Long): Long {
                return (min * MIN_TO_SEC + sec) * SEC_TO_MS
            }
        }

        override fun toString(): String {
            return "$min min, $sec sec ($ms ms)"
        }
    }

    enum class TimerState {
        STOPPED,
        RUNNING,
        PAUSED
    }

    interface TimerListener {
        fun onStart() {}
        fun onStop() {}
        fun onPause() {}
        fun onFinish() {}
    }
}

fun main() {
    var sumTime = 0L
    runBlocking {
        val timer = Timer(
            this,
            Timer.Time(0, 5),
            1000
        ).apply {
            listener = LogTimerListener
        }
        val collecting = launch {
            timer.curTime.collect {
                println("Time updated $it")
            }
        }
        sumTime = measureTimeMillis {
            runBlocking {
                timer.coroutineScope = this
                timer.start()
                delay(3000)
                timer.stop()
                timer.start()
                delay(5000)
                collecting.cancel()
            }
        }
    }
    println("Sum time: ${Timer.Time(sumTime)}")

}

private object LogTimerListener : Timer.TimerListener {
    override fun onStart() {
        println("timer started at ${System.currentTimeMillis()}")
    }

    override fun onStop() {
        println("timer stopped at ${System.currentTimeMillis()}")
    }

    override fun onPause() {
        println("timer paused at ${System.currentTimeMillis()}")
    }

    override fun onFinish() {
        println("timer finished at ${System.currentTimeMillis()}")
    }
}