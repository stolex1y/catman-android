package ru.stolexiy.catman.ui.util.binding

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.toSpanned
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import ru.stolexiy.catman.R
import ru.stolexiy.widgets.drawable.ColoredCircle
import ru.stolexiy.widgets.drawable.LinearGradientStroke

object BindingAdapters {
    @BindingAdapter("isGone")
    @JvmStatic
    fun viewIsGone(view: View, isGone: Boolean) {
        view.visibility = if (isGone)
            View.GONE
        else
            View.VISIBLE
    }

    @BindingAdapter("tintColor")
    @JvmStatic
    fun color(imageView: ImageView, color: Int?) {
        val tintColor = color ?: Color.TRANSPARENT
        imageView.drawable.setTint(tintColor)
    }

    @BindingAdapter("coloredCircle")
    @JvmStatic
    fun coloredCircle(imageView: ImageView, color: Int?) {
        imageView.setImageDrawable(
            ColoredCircle(
                imageView.context,
                color ?: Color.TRANSPARENT
            )
        )
    }

    @BindingAdapter("startIconColoredCircle")
    @JvmStatic
    fun coloredCircle(textInputLayout: TextInputLayout, color: Int?) {
        textInputLayout.startIconDrawable =
            ColoredCircle(textInputLayout.context, color ?: Color.TRANSPARENT)
    }

    @BindingAdapter("startIconIsGone")
    @JvmStatic
    fun startIconIsGone(inputLayout: TextInputLayout, isGone: Boolean) {
        inputLayout.isStartIconVisible = !isGone
    }

    @BindingAdapter("endIconIsGone")
    @JvmStatic
    fun endIconIsGone(inputLayout: TextInputLayout, isGone: Boolean) {
        inputLayout.isEndIconVisible = !isGone
    }

    @BindingAdapter("popupBackground")
    @JvmStatic
    fun popupBackground(autoCompleteTextView: MaterialAutoCompleteTextView, drawableRes: Int) {
        autoCompleteTextView.setDropDownBackgroundResource(drawableRes)
    }

    @BindingAdapter("addAsteriskToHint")
    @JvmStatic
    fun addAsteriskToHint(inputLayout: TextInputLayout, enabled: Boolean) {
        if (!enabled)
            return
        inputLayout.context.theme.obtainStyledAttributes(intArrayOf(com.google.android.material.R.attr.errorTextColor))
            .apply {
                try {
                    val color = getColor(0, 0)
                    val asterisk = '*'
                    val hint = inputLayout.hint.toString() + " " + asterisk
                    inputLayout.hint = hint.colorize(color, asterisk.toString())
                } finally {
                    recycle()
                }
            }
    }

    @BindingAdapter(value = ["textColoring", "textColoringPart"], requireAll = false)
    @JvmStatic
    fun colorizeText(textView: TextView, color: Int?, part: String?) {
        if (color == null)
            return
        val text = textView.text ?: return
        textView.text = text.colorize(textView.context.getColor(color), part ?: text)
    }

    @BindingAdapter("gradientOutline")
    @JvmStatic
    fun gradientOutline(button: Button, enabled: Boolean) {
        if (enabled) {
            button.context.apply {
                button.background = LinearGradientStroke(
                    this,
                    getColor(R.color.lightBlue),
                    getColor(R.color.lightBlue),
                    getColor(R.color.violetBlue),
                    getColor(R.color.blueShadow),
                    4f
                )
            }
        }
    }

    fun CharSequence.colorize(color: Int, part: CharSequence = this): Spanned {
        val spannable = SpannableString(this)
        val start = this.indexOf(part.toString())
        if (start == -1)
            return spannable
        spannable.setSpan(
            ForegroundColorSpan(color),
            start,
            start + part.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable.toSpanned()

    }
}