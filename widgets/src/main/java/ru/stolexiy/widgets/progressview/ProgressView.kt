package ru.stolexiy.widgets.progressview

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import ru.stolexiy.widgets.common.extension.GraphicsExtensions.getTextBounds
import ru.stolexiy.widgets.common.viewproperty.InvalidatingLayoutProperty
import ru.stolexiy.widgets.common.viewproperty.InvalidatingProperty
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.properties.Delegates

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), InvalidatingProperty.Listener,
    InvalidatingLayoutProperty.Listener {

    companion object {
        private const val DEFAULT_TEXT_SIZE: Float = 128f
        private const val DEFAULT_TEXT_COLOR: Int = 0xFFE0E3FF.toInt()

    }

    /**
     * Filling progress circle (fraction from 0 to 1).
     */
    var progress: Float by InvalidatingLayoutProperty(0f) { value -> value in 0f..1f }

    /**
     * Filling or decreasing progress circle.
     */
    var fillingUp: Boolean by InvalidatingLayoutProperty(false)

    /**
     * Text of progress color.
     */
    @get:ColorInt
    var textColor: Int by InvalidatingProperty(DEFAULT_TEXT_COLOR)

    /**
     * Text of progress size.
     */
    var textSize: Float by InvalidatingLayoutProperty(DEFAULT_TEXT_SIZE)

    /**
     * Text of progress typeface.
     */
    var textTypeface: Typeface by InvalidatingLayoutProperty(Typeface.DEFAULT)

    private val textPaint = Paint().apply {
        this.isAntiAlias = true
        this.isDither = true
        this.color = textColor
        this.textSize = this@ProgressView.textSize
        this.typeface = textTypeface
    }

    /**
     * Progress circle linear gradient start color.
     */
    @get:ColorInt
    var progressStartColor: Int by InvalidatingLayoutProperty(0xFF48C6EF.toInt())

    /**
     * Progress circle linear gradient middle color.
     */
    @get:ColorInt
    var progressMidColor: Int by InvalidatingLayoutProperty(0xFF48C6EF.toInt())

    /**
     * Progress circle linear gradient end color.
     */
    @get:ColorInt
    var progressEndColor: Int by InvalidatingLayoutProperty(0xFF6F86D6.toInt())

    /**
     * Progress circle shadow color.
     */
    @get:ColorInt
    var progressShadowColor: Int by InvalidatingProperty(0x809380EC.toInt())

    /**
     * Progress circle shadow radius.
     */
    var progressShadowRadius: Float by InvalidatingLayoutProperty(30f)

    /**
     * Progress circle width.
     */
    var progressWidth: Float by InvalidatingLayoutProperty(50f)

    /**
     * Progress circle fill or decrease clockwise or counterclockwise.
     */
    var clockwise: Boolean by InvalidatingProperty(true)

    private lateinit var progressShader: Shader

    private val progressPaint = Paint().apply {
        this.isAntiAlias = true
        this.isDither = true
        this.strokeCap = Paint.Cap.ROUND
        this.strokeWidth = progressWidth
    }

    // Service variables
    // -- circle
    private var circleInnerRadius by Delegates.notNull<Float>()
    private var circleOuterRadius by Delegates.notNull<Float>()
    private val circleCenter = PointF()
    private val circleRect = RectF()

    @VisibleForTesting
    internal var filledSector: Float = 0f

    // -- text
    var text: String by InvalidatingLayoutProperty("")
    private val textContainerRect = RectF()
    private val textBounds = Rect().also { bounds ->
        textPaint.getTextBounds("000", bounds)
    }
    private var textHeight: Int = textBounds.height()

    init {
//        context.theme.obtainStyledAttributes(
//            attrs,
//            R.styleable.TimerView,
//            0,
//            0
//        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.run(::drawCircle)
            .run(::drawTextTime)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Timber.v("onMeasure width: ${MeasureSpec.toString(widthMeasureSpec)}")
        Timber.v("onMeasure height: ${MeasureSpec.toString(heightMeasureSpec)}")

        val reqMinW: Int = paddingLeft + paddingRight +
                getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val reqMinH: Int = paddingTop + paddingBottom +
                getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        val (minW: Int, minH: Int) = calcMinSize()
        Timber.v("min width: $minW")
        Timber.v("min height: $minH")

        val w: Int = resolveSizeAndState(max(reqMinW, minW), widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(max(reqMinH, minH), heightMeasureSpec, 1)

        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        circleInnerRadius = (min(w, h) -
                maxOf(paddingLeft, paddingRight, paddingBottom, paddingTop)) / 2f -
                progressWidth -
                progressShadowRadius
        circleOuterRadius = circleInnerRadius + progressWidth + progressShadowRadius
        circleCenter.apply {
            x = paddingLeft + circleOuterRadius
            y = paddingTop + circleOuterRadius
        }
        // circle rect to draw
        circleRect.apply {
            left = circleCenter.x - circleOuterRadius + progressWidth / 2f + progressShadowRadius
            top = circleCenter.y - circleOuterRadius + progressWidth / 2f + progressShadowRadius
            right = circleCenter.x + circleOuterRadius - progressWidth / 2f - progressShadowRadius
            bottom = circleCenter.y + circleOuterRadius - progressWidth / 2f - progressShadowRadius
        }

        val textOffsetFromCenterX =
            sqrt(circleInnerRadius.pow(2) - (textHeight / 2f).pow(2))
        textContainerRect.apply {
            left = circleCenter.x - textOffsetFromCenterX
            top = circleCenter.y - textHeight / 2f
            right = circleCenter.x + textOffsetFromCenterX
            bottom = circleCenter.y + textHeight / 2f
        }

        onPropertyInvalidation()
    }

    override fun onPropertyInvalidation() {
        configurePaints()
        invalidate()
    }

    override fun onLayoutInvalidation() {
        onPropertyInvalidation()
        requestLayout()
    }

    private fun configurePaints() {
        val gradientColors = intArrayOf(
            progressStartColor,
            progressMidColor,
            progressEndColor
        )
        val gradientColorPositions = floatArrayOf(0f, 0.5f, 1f)
        progressShader = LinearGradient(
            circleRect.left, circleRect.centerY(), // start point
            circleRect.right, circleRect.centerY(), // end point
            gradientColors, gradientColorPositions, // gradient colors and positions
            Shader.TileMode.CLAMP // tile mode
        )

        progressPaint.apply {
            this.shader = progressShader
            this.strokeWidth = progressWidth
            this.style = Paint.Style.STROKE
            setShadowLayer(progressShadowRadius, 0f, 0f, progressShadowColor)
        }

        textPaint.apply {
            this.color = textColor
            this.typeface = textTypeface
            this.textSize = this@ProgressView.textSize
        }
    }

    private fun drawCircle(canvas: Canvas): Canvas {
        filledSector = getFilledSector()
        return canvas.apply {
            drawArc(circleRect, -90f, filledSector, false, progressPaint)
        }
    }

    private fun drawTextTime(canvas: Canvas): Canvas =
        canvas.apply {
            drawText(
                text,
                textContainerRect.centerX() - textBounds.width() / 2f,
                textContainerRect.centerY() + textHeight / 2f,
                textPaint
            )
        }

    private fun calcMinSize(): Pair<Int, Int> {
        textPaint.getTextBounds(text, textBounds)
        val minW = textBounds.width() + 2 * progressWidth + progressShadowRadius * 2
        return minW.toInt() to minW.toInt()
    }

    private fun getFilledSector(): Float {
        val filledSector = if (fillingUp)
            progress * 360f
        else
            -(1 - progress) * 360f

        return if (clockwise)
            filledSector
        else
            -filledSector
    }
}
