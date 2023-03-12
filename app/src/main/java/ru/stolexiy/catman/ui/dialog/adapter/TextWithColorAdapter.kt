package ru.stolexiy.catman.ui.dialog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.util.binding.BindingAdapters.color

class TextWithColorAdapter<T>(
    items: List<T>,
    context: Context,
    converter: (T) -> Item
) : ArrayAdapter<TextWithColorAdapter.Item>(context, R.layout.list_item_with_color, items.map(converter)) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_with_color, parent, false)
        view.findViewById<ImageView>(R.id.item_color).apply {
            color(this, item.color)
        }
        view.findViewById<TextView>(R.id.item_name).apply {
            text = item.name
        }
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                return null
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
        }
    }

    data class Item(
        val id: Long,
        val color: Int,
        val name: String
    ) {
        fun toCategory() = Category(id, color, name)
    }
}
