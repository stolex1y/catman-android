package ru.stolexiy.catman.ui.util.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import ru.stolexiy.catman.BR

abstract class AbstractArrayAdapter<T>(
    @LayoutRes private val itemLayout: Int,
    items: List<T>,
    context: Context,
) : ArrayAdapter<T>(
    context,
    itemLayout,
    items
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(itemLayout, parent, false)
        DataBindingUtil.bind<ViewDataBinding>(view)!!.apply {
            setVariable(BR.data, item)
            executePendingBindings()
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
}