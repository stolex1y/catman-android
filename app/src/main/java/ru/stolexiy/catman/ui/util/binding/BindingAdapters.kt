package ru.stolexiy.catman.ui.util.binding

import android.graphics.Color
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

    @BindingAdapter("startIconTintColor")
    @JvmStatic
    fun tintColor(textInputLayout: TextInputLayout, color: Int?) {
        val tintColor = color ?: Color.TRANSPARENT
        textInputLayout.startIconDrawable?.setTint(tintColor)
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

    @BindingAdapter("hintAsterisk")
    @JvmStatic
    fun addAsteriskToHint(inputLayout: TextInputLayout, color: Int) {
        val asterisk = '*'
        val hint = inputLayout.hint.toString() + " " + asterisk
        inputLayout.hint = hint.colorize(inputLayout.context.getColor(color), asterisk.toString())
    }

    @BindingAdapter(value = ["textColoring", "textColoringPart"], requireAll = false)
    @JvmStatic
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
            ForegroundColorSpan(color),
            start,
            start + part.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable.toSpanned()

    }
}