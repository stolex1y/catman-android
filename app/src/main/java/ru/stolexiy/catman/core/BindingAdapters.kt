package ru.stolexiy.catman.core

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.toSpanned
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("isGone")
fun viewIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone)
        View.GONE
    else
        View.VISIBLE
}

@BindingAdapter("color")
fun color(imageView: ImageView, color: Int) {
    imageView.drawable.setTint(color)
}

@BindingAdapter("startIconIsGone")
fun startIconIsGone(inputLayout: TextInputLayout, isGone: Boolean) {
    inputLayout.isStartIconVisible = !isGone
}

@BindingAdapter("endIconIsGone")
fun endIconIsGone(inputLayout: TextInputLayout, isGone: Boolean) {
    inputLayout.isEndIconVisible = !isGone
}

@BindingAdapter("popupBackground")
fun popupBackground(autoCompleteTextView: MaterialAutoCompleteTextView, drawableRes: Int) {
    autoCompleteTextView.setDropDownBackgroundResource(drawableRes)
}

@BindingAdapter("hintAsterisk")
fun addAsteriskToHint(inputLayout: TextInputLayout, color: Int) {
    val asterisk = '*'
    val hint = inputLayout.hint.toString() + " " + asterisk
    inputLayout.hint = hint.colorize(inputLayout.context.getColor(color), asterisk.toString())
}
@BindingAdapter(value = ["textColoring", "textColoringPart"], requireAll = false)
fun colorizeText(textView: TextView, color: Int?, part: String?) {
    if (color == null)
        return
    val text = textView.text ?: return
    textView.text = text.colorize(textView.context.getColor(color), part ?: text)
}


fun CharSequence.colorize(color: Int, part: CharSequence = this): Spanned {
    val spannable = SpannableString(this)
    val start = this.indexOf(part.toString())
    if (start == -1)
        return spannable
    spannable.setSpan(
        ForegroundColorSpan(color), start, start + part.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    return spannable.toSpanned()

}
