package ru.stolexiy.widgets.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import ru.stolexiy.widgets.R
import ru.stolexiy.widgets.common.viewproperty.InvalidatingProperty
import timber.log.Timber
import kotlin.math.min

private const val SHADOW_ALPHA = 0.4f

class ColoredCircle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), InvalidatingProperty.Listener {

    private var width: Int = 0
    private var height: Int = 0
    private val initColor: Int

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ColoredCircle,
            0,
            0
        ).apply {
            try {
                initColor = getColor(R.styleable.ColoredCircle_circleColor, Color.TRANSPARENT)
            } finally {
                recycle()
            }
        }
    }

    var circleColor: Int by InvalidatingProperty(initColor)

    private var shadowColor: Int = getShadowColor(circleColor)
    private val shadowRadius = 10f
    private val strokeWidth = 1f
    private val circle = ShapeDrawable(OvalShape()).apply {
        paint.apply {
            isAntiAlias = true
            isDither = true
            this.color = this@ColoredCircle.circleColor
            style = Paint.Style.FILL
            strokeWidth = this@ColoredCircle.strokeWidth
            setShadowLayer(shadowRadius, 0f, 0f, shadowColor)
        }
    }

    override fun onDraw(canvas: Canvas) {
        circle.draw(canvas)
    }

    override fun onPropertyInvalidation() {
        shadowColor = getShadowColor(circleColor)
        updateCircle()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        width = w - paddingLeft - paddingRight
        height = h - paddingTop - paddingBottom
        Timber.d("height: $height, width: $width")
        updateCircle()
    }

    private fun updateCircle() {
        circle.apply {
            paint.apply {
                color = this@ColoredCircle.circleColor
                setShadowLayer(shadowRadius, 0f, 0f, shadowColor)
            }
            val radius = min(
                height,
                width
            ) / 2f - shadowRadius
            val center = PointF(width / 2f, height / 2f)
            setBounds(
                (center.x - radius).toInt(),
                (center.y - radius).toInt(),
                (center.x + radius).toInt(),
                (center.y + radius).toInt()
            )
            Timber.d("x: $x, y: $y")
            Timber.d("bounds: $bounds")
            Timber.d("left: $left, right: $right, top: $top, bottom: $bottom")
        }
    }

    @ColorInt
    private fun getShadowColor(circleColor: Int): Int {
        if (circleColor == Color.TRANSPARENT)
            return Color.TRANSPARENT
        return Color.argb(
            (SHADOW_ALPHA * 255).toInt(),
            Color.red(circleColor),
            Color.blue(circleColor),
            Color.green(circleColor)
        )
    }
}
