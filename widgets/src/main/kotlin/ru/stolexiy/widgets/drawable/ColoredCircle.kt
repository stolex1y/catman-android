package ru.stolexiy.widgets.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import ru.stolexiy.widgets.common.extension.GraphicsExtensions.dpToPx
import kotlin.math.min

private const val SHADOW_ALPHA = 0.4f

class ColoredCircle(
    context: Context,
    var color: Int
) : Drawable() {

    private var shadowColor: Int = getShadowColor(this.color)
    private val shadowRadius = 5f.dpToPx(context)
    private val strokeWidth = 1f

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        strokeWidth = this@ColoredCircle.strokeWidth
    }

    override fun draw(canvas: Canvas) {
        updatePaint()
        val width: Int = bounds.width()
        val height: Int = bounds.height()
        val radius: Float = min(width, height).toFloat() / 2f - shadowRadius
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT


    private fun updatePaint() {
        shadowColor = getShadowColor(color)
        paint.apply {
            color = this@ColoredCircle.color
            setShadowLayer(shadowRadius, 0f, 0f, shadowColor)
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
