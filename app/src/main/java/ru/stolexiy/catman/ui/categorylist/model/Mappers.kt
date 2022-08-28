package ru.stolexiy.catman.ui.categorylist.model

import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.model.Purpose
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun Category.toCategoryItem() = CategoryListItem.CategoryItem(id, name, color)
fun Purpose.toPurposeItem(): CategoryListItem.PurposeItem {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).let { formatter ->
        CategoryListItem.PurposeItem(id, name, formatter.format(deadline.time), isDeadlineBurning, progress)
    }
}

fun List<Category>.toCategoryListItems(): List<CategoryListItem> {
    val result = mutableListOf<CategoryListItem>()
    this.forEach { category ->
        Timber.d("toCategoryListItems")
        result += category.toCategoryItem()
        result += category.purposes?.map(Purpose::toPurposeItem) ?: emptyList()
    }
    return result
}