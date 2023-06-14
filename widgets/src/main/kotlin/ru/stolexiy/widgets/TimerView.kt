package ru.stolexiy.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import ru.stolexiy.common.Delegates
import ru.stolexiy.common.Timer
import ru.stolexiy.widgets.common.extension.GraphicsExtensions.getTextBounds

class TimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        private const val TIME_PART_FORMAT = "%02d"
        private const val TIME_PART_SEPARATOR = ":"
        const val DEFAULT_UPDATE_TIME = 30L
    }

    private val progressView = object : ProgressView(
        context, attrs,
        R.attr.ay_progressViewStyle,
        R.style.AY_TimerView
    ) {
        private val separatorBounds = Rect()
        private val minutesBounds = Rect()
        private val secondsBounds = Rect()

        init {
            textMaxLen = 5
        }

        override fun onInvalidation() {
            super.onInvalidation()
            textPaint.getTextBounds(TIME_PART_SEPARATOR, separatorBounds)
        }

        override fun Canvas.drawProgressText() {
            val separatorStartPoint = PointF(
                (textRect.centerX() - separatorBounds.width() / 2f),
                (textRect.centerY() + separatorBounds.height() / 2f)
            )

            val minutes = TIME_PART_FORMAT.format(timer?.curTime?.min ?: 0)
            textPaint.getTextBounds(minutes, minutesBounds)
            val minutesStartPoint = PointF(
                (separatorStartPoint.x - minutesBounds.width() / 2f),
                (textRect.centerY() + minutesBounds.height() / 2f)
            )
            drawText(minutes, minutesStartPoint)

            val seconds = TIME_PART_FORMAT.format(timer?.curTime?.sec ?: 0)
            textPaint.getTextBounds(seconds, secondsBounds)
            val secondsStartPoint = PointF(
                (separatorStartPoint.x - secondsBounds.width() / 2f),
                (textRect.centerY() + secondsBounds.height() / 2f)
            )
            drawText(TIME_PART_SEPARATOR, separatorStartPoint)
            drawText(seconds, secondsStartPoint)
        }

        private fun Canvas.drawText(text: String, startPoint: PointF) {
            drawText(
                text,
                startPoint.x,
                startPoint.y,
                textPaint
            )
        }
    }

    /**
     * Progress circle linear gradient start color.
     */
    @get:ColorInt
    var progressStartColor: Int by progressView::progressStartColor

    /**
     * Progress circle linear gradient middle color.
     */
    @get:ColorInt
    var progressMidColor: Int by progressView::progressMidColor

    /**
     * Progress circle linear gradient end color.
     */
    @get:ColorInt
    var progressEndColor: Int by progressView::progressEndColor

    /**
     * Progress circle shadow color.
     */
    @get:ColorInt
    var progressShadowColor: Int by progressView::progressShadowColor

    /**
     * Progress circle shadow radius.
     */
    var progressShadowRadius: Float by progressView::progressShadowRadius

    /**
     * Progress circle width.
     */
    var progressWidth: Float by progressView::progressWidth

    /**
     * Progress circle fill or decrease clockwise or counterclockwise.
     */
    var clockwise: Boolean by progressView::clockwise

    /**
     * Filling or decreasing progress circle.
     */
    var fillingUp: Boolean by progressView::fillingUp

    /**
     * Progress track width.
     */
    var trackWidth: Float by progressView::trackWidth

    /**
     * Progress track color.
     */
    @get:ColorInt
    var trackColor: Int by progressView::trackColor

    /**
     * Text of progress color.
     */
    @get:ColorInt
    var textColor: Int by progressView::textColor

    /**
     * Text of progress size.
     */
    var textSize: Float by progressView::textSize

    /**
     * Text of progress typeface.
     */
    var textTypeface: Typeface by progressView::textTypeface

    var initTime: Timer.ImmutableTime by Delegates.lateinitObjectProperty(
        this::timer,
        Timer::initTime,
        Timer.Time(0)
    )

    var updateTime: Timer.ImmutableTime = Timer.Time(DEFAULT_UPDATE_TIME)

    var timer: Timer? = null
        private set

    private val timerListener = TimerListener()

    init {
        updateProgress()

    }

    fun timerStart() {
        timer?.start()
    }

    fun timerStop() {
        timer?.stop()
    }

    fun timerPause() {
        timer?.pause()
    }

    fun timerReset() {
        timer?.reset()
    }

    fun timerState(): Timer.State? = timer?.state

    fun setTimer(timer: Timer) {
        if (this.timer != timer) {
            this.timer = timer
            addTimerListener()
        }
    }

    private fun addTimerListener() {
        timer?.addListener(timerListener)
    }

    private fun removeTimerListener() {
        timer?.removeListener(timerListener)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addTimerListener()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeTimerListener()
    }

    @MainThread
    private fun updateProgress() {
        timer?.let {
            val initTimeMs = initTime.inMs
            val curTimeMs = it.curTime.inMs
            progressView.progress = (initTimeMs - curTimeMs) / initTimeMs.toFloat()
        }
    }

    private inner class TimerListener : Timer.Listener {
        override val updateTime: Long
            get() = this@TimerView.updateTime.inMs

        override fun onFinish(timer: Timer) {
            timer.reset()
        }

        override fun onUpdateTime(timer: Timer) {
            post {
                updateProgress()
            }
        }
    }
}
