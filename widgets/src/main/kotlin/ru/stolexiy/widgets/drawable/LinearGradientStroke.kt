package ru.stolexiy.widgets.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import ru.stolexiy.widgets.common.extension.GraphicsExtensions.dpToPx

class LinearGradientStroke(
    context: Context,
    startColor: Int,
    midColor: Int,
    endColor: Int,
    shadowColor: Int,
    strokeWidth: Float = 4f.dpToPx(context)
) : Drawable() {

    private val shadowRadius = 2f.dpToPx(context)

    private val colors = intArrayOf(startColor, midColor, endColor)

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
        setShadowLayer(shadowRadius, 0f, 0f, shadowColor)
    }

    override fun draw(canvas: Canvas) {
        val rect = RectF(copyBounds())
        rect.apply {
            left += shadowRadius
            right -= shadowRadius
            top += shadowRadius
            bottom -= shadowRadius
        }
        val gradientColorPositions = floatArrayOf(0f, 0.5f, 1f)
        val gradient = LinearGradient(
            rect.right,
            rect.height() / 2f,
            rect.left,
            rect.height() / 2f,
            colors,
            gradientColorPositions,
            Shader.TileMode.CLAMP
        )
        paint.setShader(gradient)
        canvas.drawRoundRect(rect, 100f, 100f, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}