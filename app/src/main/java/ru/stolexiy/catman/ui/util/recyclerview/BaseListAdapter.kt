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

    private var list: MutableList<T>? = null

    var longPressDragEnabled = false

    //    var itemMoveModeEnabled = false
    protected open var itemMoveEnabled: Set<Int> = emptySet()
    protected open var itemSwipeEnabled: Map<Int, Pair<Boolean, Boolean>> = emptyMap()
    protected open val itemHierarchy: Map<Int, Int> = emptyMap()
    protected val itemActionListeners: MutableMap<Int, ItemActionListener<T>> = mutableMapOf()

    protected val touchHelper: ItemTouchHelper
//    private var recyclerView: RecyclerView? = null

    init {
        touchHelper = ItemTouchHelper(TouchHelperCallback())
//        setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        touchHelper.attachToRecyclerView(recyclerView)
//        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
//        this.recyclerView = null
    }

    private inner class TouchHelperCallback : ItemTouchHelper.Callback() {
        var movingStartPosition: Int? = null
        var movingEndPosition: Int? = null

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val itemType = viewHolder.itemViewType
            val dragFlags = if (itemMoveEnabled.contains(itemType))
                ItemTouchHelper.UP or ItemTouchHelper.DOWN
            else
                0
            var swipeFlags = if (itemSwipeEnabled[itemType]?.first == true)
                ItemTouchHelper.START
            else
                0
            swipeFlags = swipeFlags or if (itemSwipeEnabled[itemType]?.second == true)
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
            if (movingStartPosition == null)
                movingStartPosition = source.adapterPosition
            if (source.itemViewType != target.itemViewType)
                return false
            val sourceItem = getItem(source.adapterPosition)
            val targetItem = getItem(target.adapterPosition)
            itemActionListeners[source.itemViewType]?.onMoveTo(sourceItem, targetItem)
            list!![source.adapterPosition] = targetItem
            list!![target.adapterPosition] = sourceItem
            movingEndPosition = target.adapterPosition
            notifyItemMoved(source.adapterPosition, target.adapterPosition)
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
                if (itemHierarchy.getOrDefault(getItemViewType(i), 0) <
                    itemHierarchy.getOrDefault(getItemViewType(start), 0)
                ) {
                    return false
                }
            }
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val item = viewHolder.adapterPosition.run(this@BaseListAdapter::getItem)
            val moveListener = itemActionListeners[viewHolder.itemViewType] ?: return
            when (direction) {
                ItemTouchHelper.START -> moveListener.onSwipeToStart(item)
                ItemTouchHelper.END -> moveListener.onSwipeToEnd(item)
                else -> Timber.w("unsupported swipe")
            }
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)

            if (viewHolder == null)
                return
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> viewHolder.itemView.alpha = 0.5f
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.alpha = 1.0f
            val moveListener = itemActionListeners[viewHolder.itemViewType] ?: return
            if (movingStartPosition != null && movingStartPosition != movingEndPosition) {
                val min = min(movingEndPosition!!, movingStartPosition!!)
                val max = max(movingEndPosition!!, movingStartPosition!!)
                moveListener.onMoveEnd(list?.subList(min, max + 1))
                movingStartPosition = null
                movingEndPosition = null
            }
        }
    }

    companion object {
        fun <T : ListItem> diffCallback() = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
                oldItem::class == newItem::class && oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
                oldItem.areContentEquals(newItem)
        }
    }

    override fun submitList(list: MutableList<T>?) {
        this.list = list
        super.submitList(list)
        Timber.d("submit list")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(layoutInflater, viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int = getLayoutIdForPosition(position)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { item ->
            holder.setData(item)
            val itemType = getItemViewType(position)
            holder.itemView.setOnClickListener { itemActionListeners[itemType]?.onClick(item) }
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
}
