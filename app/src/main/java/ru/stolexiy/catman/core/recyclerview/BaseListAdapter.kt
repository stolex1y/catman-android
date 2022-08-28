package ru.stolexiy.catman.core.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.stolexiy.catman.BR

abstract class BaseListAdapter<T : ListItem> : ListAdapter<T, BaseListAdapter.ViewHolder>(
    diffCallback()
) {

    companion object {
        fun <T : ListItem> diffCallback() = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem.areContentEquals(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int = getLayoutIdForPosition(position)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let {
            holder.setData(it)
        }
    }

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: ListItem) {
            binding.setVariable(BR.data, data)
            binding.executePendingBindings()
        }
    }
}

