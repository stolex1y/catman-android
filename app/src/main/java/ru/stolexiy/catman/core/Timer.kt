package ru.stolexiy.catman.core

import androidx.annotation.GuardedBy
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import lombok.EqualsAndHashCode
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

const val SEC_TO_MS = 1000L
const val MIN_TO_SEC = 60L
const val MIN_TO_MS = MIN_TO_SEC * SEC_TO_MS

class Timer {
    private val DEFAULT_TIME_MAX: Long = (999L * MIN_TO_SEC + 59L) * SEC_TO_MS

    private val mutex = Mutex()
    
    var maxInitTime: Long = DEFAULT_TIME_MAX
    var listener: TimerListener = LogTimerListener

    @GuardedBy("mutex")
    var initTime: Time = Time(0)
        set(value) {
            coroutineScope.launch {
                mutex.withLock {
                    if (state != TimerState.STOPPED)
                        throw IllegalStateException("Couldn't set time while timer's not stopped")
                    field = Time(min(value.ms, maxInitTime))
                }
            }
        }

    private var curTime: Time = initTime

    @GuardedBy("mutex")
    var state: TimerState = TimerState.STOPPED
        private set

    @GuardedBy("mutex")
    var coroutineContext: CoroutineContext = CoroutineName("pomodoro timer") + Dispatchers.Default
        set(value) {
            coroutineScope.launch {
                mutex.withLock {
                    if (state == TimerState.RUNNING)
                        throw IllegalStateException("Couldn't change coroutine context while timer's running")
                    field = value
                }
            }
        }

    @GuardedBy("mutex")
    var coroutineScope: CoroutineScope = CoroutineScope(coroutineContext)
        set(value) {
            coroutineScope.let {
                it.launch {
                    mutex.withLock {
                        if (state == TimerState.RUNNING)
                            throw IllegalStateException("Couldn't change coroutine scope while timer's running")
                        field = value
                    }
                }
            }
        }

    private var timerJob: Job? = null

    fun start() {
        coroutineScope.launch {
            mutex.withLock {
                when (state) {
                    TimerState.RUNNING -> return@launch
                    TimerState.STOPPED -> {
                        curTime = initTime
                        timerRun()
                    }
                    TimerState.PAUSED -> timerRun()
                    else -> throw NotImplementedError()
                }
            }
        }
    }

    fun pause() {
        coroutineScope.launch {
            mutex.withLock {
                timerJob?.cancel()
                state = TimerState.PAUSED
                coroutineScope.launch(Dispatchers.Main) {
                    listener.onPause()
                }
            }
        }
    }

    fun stop() {
        coroutineScope.launch {
            mutex.withLock {
                timerJob?.cancel()
                state = TimerState.STOPPED
                coroutineScope.launch(Dispatchers.Main) {
                    listener.onStop()
                }
            }
        }
    }

    private fun timerRun(): Job {
        return coroutineScope.launch {
            withContext(Dispatchers.Main) {
                listener.onStart()
            }
            mutex.withLock {
                state = TimerState.RUNNING
            }
            while (curTime > 0) {
                delay(1000)
                curTime--
            }
            mutex.withLock {
                state = TimerState.STOPPED
            }
            withContext(Dispatchers.Main) {
                listener.onFinish()
            }
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
            this.ms = ms
        }

        constructor(min: Long, sec: Long) : this(minAndSecToMs(min, sec)) {}

        fun setTime(ms: Long) {
            require(ms >= 0)
            this.ms = ms
            min = ms / MIN_TO_MS
            sec = ms % SEC_TO_MS
        }

        fun setTime(min: Long, sec: Long) {
            require(min >= 0 && sec >= 0)
            this.min = min
            this.sec = sec
            this.ms = minAndSecToMs(min, sec)
        }

        operator fun dec(): Time {
            return Time(max(this.ms - 1, 0))
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

    private object LogTimerListener : TimerListener {
        override fun onStart() {
            Timber.d("timer started at ${System.currentTimeMillis()}")
        }

        override fun onStop() {
            Timber.d("timer stopped at ${System.currentTimeMillis()}")
        }

        override fun onPause() {
            Timber.d("timer paused at ${System.currentTimeMillis()}")
        }

        override fun onFinish() {
            Timber.d("timer finished at ${System.currentTimeMillis()}")
        }
    }
}

fun main() {
    val timer = Timer()
    timer.initTime = Timer.Time(1, 0)
    runBlocking {
        val actualExecutionTime = measureTimeMillis {
            timer.start()
        }
        println("Timer set on 1 min finished wih ${Timer.Time(actualExecutionTime)}")
    }
}