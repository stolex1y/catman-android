package ru.stolexiy.catman.ui.categorylist.model

import android.graphics.Color
import android.os.Parcelable
import ru.stolexiy.catman.core.recyclerview.ListItem
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.ui.mapper.toDmyString

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
            get() = "$progress%"
    }
}

fun DomainCategory.toCategoryItem() = CategoryListItem.CategoryItem(id, name, color)
fun DomainPurpose.toPurposeItem(): CategoryListItem.PurposeItem {
    return CategoryListItem.PurposeItem(id, name, deadline.toDmyString(), isDeadlineBurning, progress)
}
fun List<DomainCategory>.toCategoryListItems(): List<CategoryListItem> {
    val result = mutableListOf<CategoryListItem>()
    this.forEach { category ->
        result += category.toCategoryItem()
        result += category.domainPurposes?.map(DomainPurpose::toPurposeItem) ?: emptyList()
    }
    return result
}