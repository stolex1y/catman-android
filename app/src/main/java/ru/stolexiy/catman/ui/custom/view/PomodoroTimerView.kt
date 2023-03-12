package ru.stolexiy.catman.ui.custom.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.MIN_TO_MS
import ru.stolexiy.catman.core.SEC_TO_MS
import kotlin.math.min


class PomodoroTimerView @JvmOverloads constructor(
    context : Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val TIME_MAX: Long = MIN_TO_MS * 999L + SEC_TO_MS * 59L
//    private val timer: Timer = Timer()

    private lateinit var progressShader: Shader
    /*private var progressDrawable: Drawable =
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }*/
    var progressWidth: Float = 1.0f

    var textColor: Int = Color.WHITE
    var textSize: Int = 72
    lateinit var textTypeface: Typeface

    private var progressRadius: Int = 0
    private val progressPaint: Paint = Paint().apply {
        shader = progressShader
        isAntiAlias = true
        strokeWidth = progressWidth
    }

    private val textPaint: Paint = Paint().apply {

    }


    init {
        context.withStyledAttributes(attrs, R.styleable.PomodoroTimerView) {

        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        progressRadius = (min(w, h) / 2 - paddingLeft) / 2


    }
}