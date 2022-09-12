package ru.stolexiy.catman.core

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.os.Build.VERSION
import android.view.View
import android.widget.ImageView
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.graphics.drawable.DrawableWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.resources.TextAppearance
import com.google.android.material.textfield.TextInputLayout
import ru.stolexiy.catman.R

@BindingAdapter("isGone")
fun viewIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone)
        View.GONE
    else
        View.VISIBLE
}

@BindingAdapter("shadowColor")
fun setShadowColor(imageView: ImageView, color: Int) {
//    val shadowColor = (Color.alpha(color) * 0.4).toInt() shl 24 or (color and 0xFFFFFF)
    imageView.setImageDrawable(ColorDrawable(color))
}

@BindingAdapter("startIconIsGone")
fun startIconIsGone(inputLayout: TextInputLayout, isGone: Boolean) {
    inputLayout.startIconDrawable?.setVisible(!isGone, false)
}
