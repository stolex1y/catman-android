package ru.stolexiy.catman.ui.util.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.stolexiy.catman.BR
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

abstract class BaseListAdapter<T : ListItem> : ListAdapter<T, BaseListAdapter.ViewHolder>(
    diffCallback()
) {

    var longPressDragEnabled = false
//    var itemMoveModeEnabled = false
    protected open var mItemMoveEnabled: Set<Int> = emptySet()
    protected open var mItemSwipeEnabled: Map<Int, Pair<Boolean, Boolean>> = emptyMap()
    protected open val mItemHierarchy: Map<Int, Int> = emptyMap()
    var onItemMovedListener: ((source: T, target: T) -> Unit)? = null
    var onItemSwipedToStartListener: ((source: T) -> Unit)? = null
    var onItemSwipedToEndListener: ((source: T) -> Unit)? = null
    var onItemClickListeners: MutableMap<Int, ItemClickListener> = mutableMapOf()

    protected val mTouchHelper: ItemTouchHelper
    private var mRecyclerView: RecyclerView? = null

    init {
        mTouchHelper = ItemTouchHelper(TouchHelperCallback())
//        setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mTouchHelper.attachToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null
    }

    private inner class TouchHelperCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val itemType = viewHolder.itemViewType
            val dragFlags = if (mItemMoveEnabled.contains(itemType))
                ItemTouchHelper.UP or ItemTouchHelper.DOWN
            else
                0
            var swipeFlags = if (mItemSwipeEnabled[itemType]?.first == true)
                ItemTouchHelper.START
            else
                0
            swipeFlags = swipeFlags or if (mItemSwipeEnabled[itemType]?.second == true)
                ItemTouchHelper.END
            else
                0
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun isLongPressDragEnabled(): Boolean {
            return longPressDragEnabled
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return true
        }

        override fun onMove(
            recyclerView: RecyclerView,
            source: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            if (source.itemViewType != target.itemViewType)
                return false
            val sourceItem = source.adapterPosition.run(this@BaseListAdapter::getItem)
            val targetItem = target.adapterPosition.run(this@BaseListAdapter::getItem)
            onItemMovedListener?.invoke(sourceItem, targetItem)
            this@BaseListAdapter.notifyItemMoved(source.adapterPosition, target.adapterPosition)
            return true
        }

        override fun canDropOver(
            recyclerView: RecyclerView,
            current: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            if (current.itemViewType != target.itemViewType) {
                return false
            }
            val start = min(current.adapterPosition, target.adapterPosition)
            val end = max(current.adapterPosition, target.adapterPosition)
            for (i in start..end) {
                if (mItemHierarchy.getOrDefault(getItemViewType(i), 0) <
                    mItemHierarchy.getOrDefault(getItemViewType(start), 0)) {
                    return false
                }
            }
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val item = viewHolder.adapterPosition.run(this@BaseListAdapter::getItem)
            when (direction) {
                ItemTouchHelper.START -> onItemSwipedToStartListener?.invoke(item)
                ItemTouchHelper.END -> onItemSwipedToEndListener?.invoke(item)
                else -> Timber.w("unsupported swipe")
            }
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)

            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.itemView?.alpha = 0.5f
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.alpha = 1.0f
        }
    }

    companion object {
        fun <T : ListItem> diffCallback() = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem::class == newItem::class && oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem.areContentEquals(newItem)
        }
    }

    override fun submitList(list: MutableList<T>?) {
        super.submitList(list)
        Timber.d("submit list")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int = getLayoutIdForPosition(position)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { item ->
            holder.setData(item)
            val itemType = getItemViewType(position)
            holder.itemView.setOnClickListener { onItemClickListeners[itemType]?.onClick(item) }
        }
    }

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: ListItem) {
            binding.setVariable(BR.data, data)
            binding.executePendingBindings()
        }
    }

    fun interface ItemClickListener {
        fun onClick(item: ListItem)
    }
}

