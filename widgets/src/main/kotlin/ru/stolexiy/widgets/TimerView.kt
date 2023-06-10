package ru.stolexiy.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.MainThread
import kotlinx.coroutines.Dispatchers
import ru.stolexiy.common.TimeConstants.MIN_TO_MS
import ru.stolexiy.common.Timer

class TimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
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

    private val progressView = ProgressView(context, attrs)

    private val timerListener = TimerListener()

    private val timer = Timer(
        Dispatchers.IO //TODO set this field from caller
    ).apply {
        listener = timerListener
        initTime = Timer.Time(DEFAULT_INIT_TIME)
        updateTime = Timer.Time(DEFAULT_UPDATE_TIME)
    }

    var initTime: Timer.Time by timer::initTime
    var updateTime: Timer.Time by timer::updateTime
    var clockwise: Boolean by progressView::clockwise

    init {
        progressView.fillingUp = false
        progressView.clockwise = true
        updateText()
        updateProgress()
        this.addView(progressView)
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
//        progressView.text = formatTime(timer.curTime)
    }

    @MainThread
    fun updateProgress() {
        val initTimeMs = initTime.inMs
        val curTimeMs = timer.curTime.inMs
        progressView.progress = (initTimeMs - curTimeMs) / initTimeMs.toFloat()
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
