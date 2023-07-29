package ru.stolexiy.catman.ui.categorylist.model

import ru.stolexiy.catman.ui.util.recyclerview.ListItem
import ru.stolexiy.common.DateUtils
import ru.stolexiy.common.DateUtils.toString
import java.time.ZonedDateTime

sealed class CategoryListItem : ListItem {
    data class CategoryItem(
        override val id: Long,
        val name: String,
        val color: Int
    ) : CategoryListItem()

    class PurposeItem(
        override val id: Long,
        val name: String,
        val isBurning: Boolean,
        var priority: Int,
        deadline: ZonedDateTime,
        progress: Double
    ) : CategoryListItem() {
        val deadline: String = deadline.toString(DateUtils.DMY_DATE)
        val progress: Float = (progress * 100).toFloat()
    }
}
