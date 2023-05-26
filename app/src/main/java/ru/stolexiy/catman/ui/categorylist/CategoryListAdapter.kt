package ru.stolexiy.catman.ui.categorylist

import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.util.recyclerview.BaseListAdapter
import ru.stolexiy.catman.ui.util.recyclerview.ItemActionListener
import kotlin.reflect.KClass

class CategoryListAdapter : BaseListAdapter<CategoryListItem>() {

    override val itemHierarchy: Map<Int, Int> = mapOf(
        getItemType(CategoryListItem.CategoryItem::class) to 1,
        getItemType(CategoryListItem.PurposeItem::class) to 2
    )

    override var itemMoveEnabled: Set<Int> = mutableSetOf(
        getItemType(CategoryListItem.PurposeItem::class),
    )
    override var itemSwipeEnabled: Map<Int, Pair<Boolean, Boolean>> = mutableMapOf(
        getItemType(CategoryListItem.PurposeItem::class) to Pair(true, true)
    )

    override fun getLayoutIdForPosition(position: Int): Int =
        getItem(position)::class.run(this::getItemType)

    fun setCategoryActionListener(listener: ItemActionListener<CategoryListItem>) {
        itemActionListeners[getItemType(CategoryListItem.CategoryItem::class)] = listener
    }

    fun setPurposeActionListener(listener: ItemActionListener<CategoryListItem>) {
        itemActionListeners[getItemType(CategoryListItem.PurposeItem::class)] = listener
    }

    private fun getItemType(type: KClass<out CategoryListItem>): Int {
        return when (type) {
            CategoryListItem.CategoryItem::class -> R.layout.list_categories_category_item
            CategoryListItem.PurposeItem::class -> R.layout.list_categories_purpose_item
            else -> throw IllegalArgumentException()
        }
    }
}
