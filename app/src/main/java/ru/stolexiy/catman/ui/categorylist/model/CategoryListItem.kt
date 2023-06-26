package ru.stolexiy.catman.ui.categorylist.model

import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
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

    data class PurposeItem(
        override val id: Long,
        val name: String,
        private val _deadline: ZonedDateTime,
        val isBurning: Boolean,
        val progress: Float
    ) : CategoryListItem() {
        val deadline: String
            get() = _deadline.toString(DateUtils.DMY_DATE)
    }
}

fun DomainCategory.toCategoryItem() = CategoryListItem.CategoryItem(id, name, color)
fun DomainPurpose.toPurposeItem(): CategoryListItem.PurposeItem {
    return CategoryListItem.PurposeItem(
        id,
        name,
        deadline,
        isDeadlineBurning,
        progress / 100f
    )
}

fun Map<DomainCategory, List<DomainPurpose>>.toCategoryListItems(): List<CategoryListItem> {
    val result = mutableListOf<CategoryListItem>()
    this.forEach { mapEntry ->
        result += mapEntry.key.toCategoryItem()
        result += mapEntry.value.map(DomainPurpose::toPurposeItem)
    }
    return result
}
