package ru.stolexiy.widgets.common.extension

import android.content.Context
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.util.TypedValue

internal object GraphicsExtensions {
    infix fun Int.to(y: Int) = Point(this, y)
    infix fun Float.to(y: Float) = PointF(this, y)

    fun Float.dpToPx(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
    )

    fun Int.dpToPx(context: Context) = this.toFloat().dpToPx(context)

    fun Float.spToPx(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics
    )

    fun Int.spToPx(context: Context) = this.toFloat().spToPx(context)

    fun Float.dpToSp(context: Context) =
        (this.dpToPx(context) / context.resources.displayMetrics.scaledDensity).toInt()

    fun Int.dpToSp(context: Context) = this.toFloat().dpToSp(context)

    fun Paint.getTextBounds(text: String, bounds: Rect) =
        this.getTextBounds(text, 0, text.length, bounds)

    fun color(r: Int, g: Int, b: Int, a: Float): Int =
        ((a * 255.0f + 0.5f).toInt() shl 24) or
                (r * 255 shl 16) or
                (g * 255 shl 8) or
                (b * 255)
}
