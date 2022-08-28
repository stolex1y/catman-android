package ru.stolexiy.catman.ui.categorylist.model

import android.graphics.Color
import ru.stolexiy.catman.core.recyclerview.ListItem

sealed class CategoryListItem : ListItem {
    data class CategoryItem(
        override val id: Long,
        val name: String,
        val color: Int
    ) : CategoryListItem()

    data class PurposeItem(
        override val id: Long,
        val name: String,
        val deadline: String,
        val isBurning: Boolean,
        val progress: Int
    ) : CategoryListItem() {
        val textProgress: String
            get() = "$progress %"
    }
}