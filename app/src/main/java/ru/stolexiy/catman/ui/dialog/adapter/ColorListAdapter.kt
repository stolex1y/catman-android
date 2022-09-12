package ru.stolexiy.catman.ui.dialog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.setShadowColor
import ru.stolexiy.catman.ui.dialog.model.Color

class ColorListAdapter(
    context: Context
) : ArrayAdapter<ColorListAdapter.ColorItem>(context, 0) {

    init {
        addAll(Color.values().map {
            ColorItem(it.color, context.resources.getString(it.nameRes))
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.colors_list_color_item, parent, false)
//        view.findViewById<ShapeableImageView>(R.id.color).apply {
//            setShadowColor(this, item.color)
//        }
//        view.findViewById<TextView>(R.id.name).apply {
//            text = item.name
//        }
        return view
    }

    data class ColorItem(
        val color: Int,
        val name: String
    )
}