package ru.stolexiy.catman.ui.dialog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.color
import ru.stolexiy.catman.ui.dialog.purpose.model.Category

class TextWithColorAdapter<T>(
    items: List<T>,
    context: Context,
    converter: (T) -> Item
) : ArrayAdapter<TextWithColorAdapter.Item>(context, R.layout.item_with_color_list_item, items.map(converter)) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_with_color_list_item, parent, false)
        view.findViewById<ImageView>(R.id.item_color).apply {
            color(this, item.color)
        }
        view.findViewById<TextView>(R.id.item_name).apply {
            text = item.name
        }
        return view
    }

    data class Item(
        val id: Long,
        val color: Int,
        val name: String
    ) {
        fun toCategory() = Category(id, color, name)
    }
}
