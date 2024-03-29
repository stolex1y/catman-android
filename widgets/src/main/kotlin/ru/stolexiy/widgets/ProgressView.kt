package ru.stolexiy.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.res.getIntOrThrow
import androidx.customview.view.AbsSavedState
import ru.stolexiy.catman.ui.common.readBooleanCompat
import ru.stolexiy.catman.ui.common.writeBooleanCompat
import ru.stolexiy.widgets.common.extension.GraphicsExtensions.getTextBounds
import ru.stolexiy.widgets.common.viewproperty.InvalidatingLayoutProperty
import ru.stolexiy.widgets.common.viewproperty.InvalidatingProperty
import java.io.Serializable
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.properties.Delegates

open class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.ay_progressViewStyle,
    defStyleRes: Int = R.style.AY_ProgressView
) : View(context, attrs, 0, 0), InvalidatingProperty.Listener,
    InvalidatingLayoutProperty.Listener {

    private val _fillingUp: Boolean

    @ColorInt
    private val _progressStartColor: Int

    @ColorInt
    private val _progressMidColor: Int

    @ColorInt
    private val _progressEndColor: Int

    @ColorInt
    private val _progressShadowColor: Int
    private val _progressShadowRadius: Float
    private val _progressWidth: Float
    private val _clockwise: Boolean

    @ColorInt
    private val _textColor: Int
    private val _textSize: Float
    private val _textTypeface: Typeface
    private val _textMaxLen: Int

    private val _trackWidth: Float

    @ColorInt
    private val _trackColor: Int

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                _fillingUp = getBoolean(R.styleable.ProgressView_ay_fillingUp, true)
                _clockwise = getBoolean(R.styleable.ProgressView_ay_clockwise, true)
                val progressColor =
                    getColor(R.styleable.ProgressView_ay_progressColor, Color.TRANSPARENT)
                _progressStartColor =
                    getColor(R.styleable.ProgressView_ay_progressStartColor, progressColor)
                _progressMidColor =
                    getColor(R.styleable.ProgressView_ay_progressMidColor, _progressStartColor)
                _progressEndColor =
                    getColor(R.styleable.ProgressView_ay_progressEndColor, _progressMidColor)
                _progressWidth = getDimension(R.styleable.ProgressView_ay_progressWidth, 50f)
                _progressShadowColor =
                    getColor(R.styleable.ProgressView_ay_progressColor, Color.TRANSPARENT)
                _progressShadowRadius =
                    getDimension(R.styleable.ProgressView_ay_progressShadowRadius, 30f)
                _trackColor =
                    getColor(R.styleable.ProgressView_ay_trackColor, Color.TRANSPARENT)
                _trackWidth = getDimension(R.styleable.ProgressView_ay_trackWidth, 0f)
                _textColor = getColorOrThrow(R.styleable.ProgressView_android_textColor)
                _textSize = getDimensionOrThrow(R.styleable.ProgressView_android_textSize)
                _textTypeface = ResourcesCompat.getFont(
                    context,
                    getResourceId(R.styleable.ProgressView_android_fontFamily, -1)
                ) ?: Typeface.MONOSPACE
                _textMaxLen = getIntOrThrow(R.styleable.ProgressView_ay_textMaxLen)
            } finally {
                recycle()
            }
        }
    }

    /**
     * Filling progress circle (fraction from 0 to 1).
     */
    var progress: Float by InvalidatingProperty(0f) { value -> value in 0f..1f }

    /**
     * Filling or decreasing progress circle.
     */
    var fillingUp: Boolean by InvalidatingProperty(_fillingUp)

    /**
     * Progress circle linear gradient start color.
     */
    @get:ColorInt
    var progressStartColor: Int by InvalidatingProperty(_progressStartColor)

    /**
     * Progress circle linear gradient middle color.
     */
    @get:ColorInt
    var progressMidColor: Int by InvalidatingProperty(_progressMidColor)

    /**
     * Progress circle linear gradient end color.
     */
    @get:ColorInt
    var progressEndColor: Int by InvalidatingProperty(_progressEndColor)

    /**
     * Progress circle shadow color.
     */
    @get:ColorInt
    var progressShadowColor: Int by InvalidatingProperty(_progressShadowColor)

    /**
     * Progress circle shadow radius.
     */
    var progressShadowRadius: Float by InvalidatingLayoutProperty(_progressShadowRadius)

    /**
     * Progress circle width.
     */
    var progressWidth: Float by InvalidatingLayoutProperty(_progressWidth)

    /**
     * Progress circle fill or decrease clockwise or counterclockwise.
     */
    var clockwise: Boolean by InvalidatingProperty(_clockwise)

    /**
     * Progress track width.
     */
    var trackWidth: Float by InvalidatingProperty(_trackWidth)

    /**
     * Progress track color.
     */
    @get:ColorInt
    var trackColor: Int by InvalidatingProperty(_trackColor)

    /**
     * Text of progress color.
     */
    @get:ColorInt
    var textColor: Int by InvalidatingProperty(_textColor)

    /**
     * Text of progress size.
     */
    var textSize: Float by InvalidatingLayoutProperty(_textSize)

    /**
     * Text of progress typeface.
     */
    var textTypeface: Typeface by InvalidatingLayoutProperty(_textTypeface)

    var textCalculator: TextCalculator? by InvalidatingLayoutProperty(null)

    var textMaxLen by InvalidatingLayoutProperty(_textMaxLen) { it > 0 }

    var text: String by InvalidatingLayoutProperty("")

    protected var textMaxHeight: Int = 0
        private set
    protected var textMaxWidth: Int = 0
        private set
    protected val letterBounds = Rect()
    protected val textRect = RectF()
    protected val textBounds = Rect()

    protected val textPaint = Paint().apply {
        this.isAntiAlias = true
        this.isDither = true
        this.color = textColor
        this.textSize = this@ProgressView.textSize
        this.typeface = textTypeface
    }

    private lateinit var progressShader: Shader

    private val progressPaint = Paint().apply {
        this.isAntiAlias = true
        this.isDither = true
        this.strokeCap = Paint.Cap.ROUND
        this.style = Paint.Style.STROKE
    }

    private val progressTrackPaint = Paint().apply {
        this.isAntiAlias = true
        this.isDither = true
        this.style = Paint.Style.STROKE
    }

    // Service variables
    // -- circle
    private var circleInnerRadius by Delegates.notNull<Float>()
    private var circleOuterRadius by Delegates.notNull<Float>()
    private val circleCenter = PointF()
    private val circleRect = RectF()

    @VisibleForTesting
    var filledSector: Float = 0f
        private set

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            drawCircle()
            drawProgressText()
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        onInvalidation()
        val (minW: Int, minH: Int) = calcMinSize()

        var w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        var h: Int = resolveSizeAndState(minH, heightMeasureSpec, 1)

        val desiredW: Int = MeasureSpec.getSize(w)
        val desiredH: Int = MeasureSpec.getSize(h)

        if (desiredW != desiredH) {
            val max = max(desiredW, desiredH)
            w = resolveSizeAndState(max, widthMeasureSpec, 1)
            h = resolveSizeAndState(max, heightMeasureSpec, 1)
        }

        setMeasuredDimension(w, h)
    }

    public override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val circleHalfWidthWithShadow = progressWidth + 2 * progressShadowRadius + 2
        circleInnerRadius = (min(w, h) -
                maxOf(paddingLeft, paddingRight, paddingBottom, paddingTop)) / 2f -
                circleHalfWidthWithShadow
        circleOuterRadius = circleInnerRadius + circleHalfWidthWithShadow
        circleCenter.apply {
            x = w / 2f
            y = h / 2f
        }
        // circle rect to draw
        circleRect.apply {
            left = circleCenter.x - circleOuterRadius + circleHalfWidthWithShadow / 2f
            top = circleCenter.y - circleOuterRadius + circleHalfWidthWithShadow / 2f
            right = circleCenter.x + circleOuterRadius - circleHalfWidthWithShadow / 2f
            bottom = circleCenter.y + circleOuterRadius - circleHalfWidthWithShadow / 2f
        }

        textRect.apply {
            left = circleCenter.x - textMaxWidth / 2f
            right = circleCenter.x + textMaxWidth / 2f
            top = circleCenter.y - textMaxHeight / 2f
            bottom = circleCenter.y + textMaxHeight / 2f
        }
    }

    override fun onPropertyInvalidation() {
        onInvalidation()
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val parcelable = super.onSaveInstanceState()
        return parcelable?.let {
            SavedState(parcelable).also {
                it.progress = progress
                it.text = text
                it.textCalculator = textCalculator
                it.fillingUp = fillingUp
                it.progressStartColor = progressStartColor
                it.progressMidColor = progressMidColor
                it.progressEndColor = progressEndColor
                it.progressShadowColor = progressShadowColor
                it.progressShadowRadius = progressShadowRadius
                it.progressWidth = progressWidth
                it.clockwise = clockwise
                it.textColor = textColor
                it.textSize = textSize
//            it.textTypeface = textTypeface
                it.textMaxLen = textMaxLen
                it.trackWidth = trackWidth
                it.trackColor = trackColor
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                this.progress = progress
                this.text = text
                this.textCalculator = textCalculator
                this.fillingUp = fillingUp
                this.progressStartColor = progressStartColor
                this.progressMidColor = progressMidColor
                this.progressEndColor = progressEndColor
                this.progressShadowColor = progressShadowColor
                this.progressShadowRadius = progressShadowRadius
                this.progressWidth = progressWidth
                this.clockwise = clockwise
                this.textColor = textColor
                this.textSize = textSize
//            this.textTypeface = textTypeface
                this.textMaxLen = textMaxLen
                this.trackWidth = trackWidth
                this.trackColor = trackColor
            }

            else -> {
                super.onRestoreInstanceState(state)
            }
        }
    }

    override fun onLayoutInvalidation() {
        onInvalidation()
        requestLayout()
        invalidate()
    }

    protected open fun onInvalidation() {
        updateFilledSector()
        updateMaxTextBounds()
        updateText()
        configurePaints()
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
            setShadowLayer(progressShadowRadius, 0f, 0f, progressShadowColor)
        }

        progressTrackPaint.apply {
            this.strokeWidth = trackWidth
            this.color = trackColor
        }

        textPaint.apply {
            this.color = textColor
            this.typeface = textTypeface
            this.textSize = this@ProgressView.textSize
        }
    }

    private fun Canvas.drawCircle() {
        if (trackWidth > 0) {
            drawCircle(
                circleRect.centerX(), circleRect.centerY(),
                circleRect.width() / 2f,
                progressTrackPaint
            )
        }
        drawArc(circleRect, -90f, filledSector, false, progressPaint)
    }

    private fun calcMinSize(): Pair<Int, Int> {
        val innerRadius = sqrt((textMaxWidth / 2f).pow(2) + (textMaxHeight / 2f).pow(2))
        val minW = (progressWidth + 2 * progressShadowRadius + innerRadius) * 2
        return max(minimumWidth, minW.toInt()) to max(minimumHeight, minW.toInt())
    }

    private fun updateFilledSector() {
        filledSector = if (fillingUp)
            progress * 360f
        else
            -(1 - progress) * 360f

        if (!clockwise)
            filledSector *= -1
    }

    private fun updateMaxTextBounds() {
        textPaint.getTextBounds("MM", letterBounds)
        letterBounds.right = letterBounds.left + (letterBounds.width() / 2f).roundToInt()
        textMaxWidth = letterBounds.width() * textMaxLen
        textMaxHeight = letterBounds.height()
    }

    protected open fun Canvas.drawProgressText() {
        textPaint.getTextBounds(text, textBounds)
        drawText(
            text,
            textRect.centerX() - textBounds.width() / 2f,
            textRect.centerY() + textBounds.height() / 2f,
            textPaint
        )
    }

    private fun updateText() {
        textCalculator?.let {
            text = it.calc(progress)
        }
    }

    fun interface TextCalculator : Serializable {

        fun calc(progress: Float): String
    }

    private class SavedState : AbsSavedState {
        var progress: Float by Delegates.notNull()
        lateinit var text: String
        var textCalculator: TextCalculator? = null
        var fillingUp: Boolean by Delegates.notNull()

        @get:ColorInt
        var progressStartColor: Int by Delegates.notNull()

        @get:ColorInt
        var progressMidColor: Int by Delegates.notNull()

        @get:ColorInt
        var progressEndColor: Int by Delegates.notNull()

        @get:ColorInt
        var progressShadowColor: Int by Delegates.notNull()
        var progressShadowRadius: Float by Delegates.notNull()
        var progressWidth: Float by Delegates.notNull()
        var clockwise: Boolean by Delegates.notNull()

        @get:ColorInt
        var textColor: Int by Delegates.notNull()
        var textSize: Float by Delegates.notNull()

        //        lateinit var textTypeface: Typeface
        var textMaxLen: Int by Delegates.notNull()

        var trackWidth: Float by Delegates.notNull()

        @get:ColorInt
        var trackColor: Int by Delegates.notNull()

        constructor(superState: Parcelable) : super(superState)
        constructor(source: Parcel, classLoader: ClassLoader?) : super(source, classLoader) {
            progress = source.readFloat()
            text = source.readString() ?: ""
            textCalculator =
                source.readTypedObject(TextCalculatorParcelable.CREATOR)?.textCalculator
            fillingUp = source.readBooleanCompat()
            progressStartColor = source.readInt()
            progressMidColor = source.readInt()
            progressEndColor = source.readInt()
            progressShadowColor = source.readInt()
            progressShadowRadius = source.readFloat()
            progressWidth = source.readFloat()
            clockwise = source.readBooleanCompat()
            textColor = source.readInt()
            textSize = source.readFloat()
//            textTypeface = Typeface.createFromAsset(
//                getContext().getAssets(), "fonts/" + fontName);
            textMaxLen = source.readInt()
            trackWidth = source.readFloat()
            trackColor = source.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeFloat(progress)
            dest.writeString(text)
            dest.writeTypedObject(TextCalculatorParcelable(textCalculator), 0)
            dest.writeBooleanCompat(fillingUp)
            dest.writeInt(progressStartColor)
            dest.writeInt(progressMidColor)
            dest.writeInt(progressEndColor)
            dest.writeInt(progressShadowColor)
            dest.writeFloat(progressShadowRadius)
            dest.writeFloat(progressWidth)
            dest.writeBooleanCompat(clockwise)
            dest.writeInt(textColor)
            dest.writeFloat(textSize)
//            textTypeface = Typeface.createFromAsset(
//                getContext().getAssets(), "fonts/" + fontName);
            dest.writeInt(textMaxLen)
            dest.writeFloat(trackWidth)
            dest.writeInt(trackColor)
        }

        companion object CREATOR : Parcelable.ClassLoaderCreator<SavedState> {

            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel, null)
            }

            override fun createFromParcel(parcel: Parcel, classLoader: ClassLoader?): SavedState {
                return SavedState(parcel, classLoader)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }

        private data class TextCalculatorParcelable(
            val textCalculator: TextCalculator?
        ) : Parcelable {
            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeBundle(Bundle().apply {
                    putSerializable(
                        TEXT_CALCULATOR,
                        textCalculator
                    )
                })
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<TextCalculatorParcelable> {

                private const val TEXT_CALCULATOR = "TEXT_CALCULATOR"
                override fun createFromParcel(parcel: Parcel): TextCalculatorParcelable? {
                    return parcel.readBundle(CREATOR::class.java.classLoader)
                        ?.getSerializable(TEXT_CALCULATOR)?.let {
                            TextCalculatorParcelable(it as TextCalculator)
                        }
                }

                override fun newArray(size: Int): Array<TextCalculatorParcelable?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }
}
