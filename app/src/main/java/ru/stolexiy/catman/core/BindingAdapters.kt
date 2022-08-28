package ru.stolexiy.catman.core

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.graphics.drawable.DrawableWrapper
import androidx.databinding.BindingAdapter
import com.google.android.material.resources.TextAppearance
import ru.stolexiy.catman.R

@BindingAdapter("isGone")
fun viewIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone)
        View.GONE
    else
        View.VISIBLE
}

@BindingAdapter("colorWithShadow")
fun setColorWithShadow(imageView: ImageView, color: Int) {
    imageView.setColorFilter(color)
    val shadowColor = (Color.alpha(color) * 0.4).toInt() shl 24 or (color and 0xFFFFFF)
    imageView.background.setTint(shadowColor)
}