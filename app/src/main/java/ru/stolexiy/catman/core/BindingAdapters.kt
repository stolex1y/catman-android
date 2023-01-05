package ru.stolexiy.catman.core

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
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
    inputLayout.refreshStartIconDrawableState()
}

@BindingAdapter("endIconIsGone")
fun endIconIsGone(inputLayout: TextInputLayout, isGone: Boolean) {
    inputLayout.isEndIconVisible = !isGone
}

@BindingAdapter("popupBackground")
fun popupBackground(autoCompleteTextView: MaterialAutoCompleteTextView, drawableRes: Int) {
    autoCompleteTextView.setDropDownBackgroundResource(drawableRes)
}