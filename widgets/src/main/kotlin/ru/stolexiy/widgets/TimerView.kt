package ru.stolexiy.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import ru.stolexiy.common.Timer
import ru.stolexiy.widgets.common.extension.GraphicsExtensions.getTextBounds
import ru.stolexiy.widgets.common.extension.GraphicsExtensions.spToPx
import ru.stolexiy.widgets.common.viewproperty.InvalidatingLayoutProperty
import kotlin.math.ceil
import kotlin.math.roundToInt

class TimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs), InvalidatingLayoutProperty.Listener {

    companion object {
        private const val TIME_PART_FORMAT = "%02d"
        private const val TIME_PART_SEPARATOR = ":"
        const val DEFAULT_UPDATE_TIME = 30L

        private fun Timer.ImmutableTime.secCeil(): Int {
            return ceil(this.sec.toFloat() + this.ms / 1000f).toInt()
        }
    }

    @IdRes
    private val progressViewStyle: Int

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimerView,
            R.attr.ay_timerViewStyle,
            R.style.AY_TimerView
        ).apply {
            try {
                progressViewStyle = getResourceId(
                    R.styleable.TimerView_ay_progressViewStyle,
                    R.attr.ay_progressViewStyle
                )
            } finally {
                recycle()
            }
        }
    }

    private val progressView = object : ProgressView(
        context,
        attrs,
        R.styleable.TimerView_ay_progressViewStyle,
        progressViewStyle
    ) {
        private val separatorBounds = Rect()
        private val minutesBounds = Rect()
        private val secondsBounds = Rect()
        private var letterSpacing: Float = 0f

        init {
            textMaxLen = 5
        }

        override fun onInvalidation() {
            super.onInvalidation()
            textPaint.getTextBounds(TIME_PART_SEPARATOR, separatorBounds)
            letterSpacing = 10.spToPx(context)
        }

        override fun Canvas.drawProgressText() {
            val separatorStartPoint = PointF(
                (textRect.centerX() - separatorBounds.width() / 2f),
                (textRect.centerY() + separatorBounds.height() / 2f)
            )

            val minutes = TIME_PART_FORMAT.format(timer?.curTime?.min ?: 0)
            textPaint.getTextBounds(minutes, minutesBounds)
            val minutesStartPoint = PointF(
                (separatorStartPoint.x - minutesBounds.width() - letterSpacing),
                (textRect.centerY() + minutesBounds.height() / 2f)
            )
            drawText(minutes, minutesStartPoint)

            val seconds = TIME_PART_FORMAT.format(timer?.curTime?.secCeil() ?: 0)
            textPaint.getTextBounds(seconds, secondsBounds)
            val secondsStartPoint = PointF(
                (separatorStartPoint.x + separatorBounds.width() + letterSpacing),
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

    var updateTime: Timer.ImmutableTime = Timer.Time(DEFAULT_UPDATE_TIME)

    var timer: Timer? by InvalidatingLayoutProperty(null)
        private set

    private val timerListener = TimerListener()

    private val center = PointF()

    init {
        addView(progressView)
        updateProgress()
    }

    override fun onLayoutInvalidation() {
        progressView.requestLayout()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addTimerListener()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeTimerListener()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        center.apply {
            x = (r - l) / 2f
            y = (b - t) / 2f
        }
        progressView.layout(
            (center.x - progressView.measuredWidth / 2f).roundToInt(),
            (center.y - progressView.measuredHeight / 2f).roundToInt(),
            (center.x + progressView.measuredWidth / 2f).roundToInt(),
            (center.y + progressView.measuredHeight / 2f).roundToInt(),
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildWithMargins(
            progressView,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )
        val w: Int = resolveSizeAndState(progressView.measuredWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(progressView.measuredHeight, heightMeasureSpec, 1)
        setMeasuredDimension(w, h)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    fun addTimerToView(timer: Timer) {
        removeTimerListener()
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

    @MainThread
    private fun updateProgress() {
        timer?.let {
            val initTimeMs = it.initTime.inMs
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
