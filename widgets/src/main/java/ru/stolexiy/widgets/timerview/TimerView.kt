package ru.stolexiy.widgets.timerview

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.MainThread
import ru.stolexiy.common.TimeConstants.MIN_TO_MS
import ru.stolexiy.common.TimeConstants.SEC_TO_MS
import ru.stolexiy.common.Timer
import ru.stolexiy.widgets.progressview.ProgressView
import kotlin.math.ceil

class TimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ProgressView(context, attrs, defStyleAttr) {

    companion object {
        private const val TIME_FORMAT: String = "%02d:%02d"

        private const val DEFAULT_UPDATE_TIME: Long = 30
        private const val DEFAULT_INIT_TIME: Long = MIN_TO_MS * 5

        @JvmStatic
        private fun formatTime(time: Timer.Time): String {
            return time.let {
                TIME_FORMAT.format(it.min, it.sec)
            }
        }
    }

    private val timerListener = TimerListener()

    private val timer = Timer(
        initTime = Timer.Time(DEFAULT_INIT_TIME),
        updateTime = Timer.Time(DEFAULT_UPDATE_TIME)
    ).apply {
        listener = timerListener
    }

    var initTime: Timer.Time by timer::initTime
    var updateTime: Timer.Time by timer::updateTime

    init {
        fillingUp = false
        clockwise = true
        updateText()
        updateProgress()
    }

    fun timerStart() {
        timer.start()
    }

    fun timerStop() {
        timer.stop()
    }

    fun timerPause() {
        timer.pause()
    }

    fun timerReset() {
        timer.reset()
    }

    fun timerState(): Timer.State = timer.state

    @MainThread
    private fun updateText() {
        text = formatTime(timer.curTime)
    }

    @MainThread
    fun updateProgress() {
        val initTimeMs = initTime.inMs
        val curTimeMs = timer.curTime.inMs
        progress = (initTimeMs - curTimeMs) / initTimeMs.toFloat()
    }

    private inner class TimerListener : Timer.TimerListener {
        override fun onFinish(timer: Timer) {
            timer.reset()
        }

        override fun onUpdateTime(timer: Timer) {
            post {
                updateText()
                updateProgress()
            }
        }
    }
}