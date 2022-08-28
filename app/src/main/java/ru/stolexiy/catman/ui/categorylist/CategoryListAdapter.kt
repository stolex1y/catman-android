package ru.stolexiy.catman.ui.categorylist

import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.recyclerview.BaseListAdapter
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem

class CategoryListAdapter :
        BaseListAdapter<CategoryListItem>() {
    override fun getLayoutIdForPosition(position: Int): Int = when (getItem(position)) {
        is CategoryListItem.CategoryItem -> R.layout.category_list_category_item
        is CategoryListItem.PurposeItem -> R.layout.category_list_purpose_item
    }
}