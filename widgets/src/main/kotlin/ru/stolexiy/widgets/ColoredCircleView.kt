package ru.stolexiy.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import ru.stolexiy.widgets.common.viewproperty.InvalidatingProperty
import ru.stolexiy.widgets.drawable.ColoredCircle

class ColoredCircleView @JvmOverloads constructor(
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
                initColor = getColor(R.styleable.ColoredCircle_ay_circleColor, Color.TRANSPARENT)
            } finally {
                recycle()
            }
        }
    }

    var circleColor: Int by InvalidatingProperty(initColor)

    private val drawable = ColoredCircle(context, circleColor)

    override fun onDraw(canvas: Canvas) {
        drawable.draw(canvas)
    }

    override fun onPropertyInvalidation() {
        updateCircle()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        width = w - paddingLeft - paddingRight
        height = h - paddingTop - paddingBottom
        updateCircle()
    }

    private fun updateCircle() {
        drawable.apply {
            color = circleColor
            setBounds(
                0,
                0,
                width,
                height
            )
        }
    }
}
