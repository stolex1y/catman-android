package ru.stolexiy.catman.ui.categorylist

import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.util.udf.IEvent

sealed interface CategoryListEvent : IEvent {
    object Load : CategoryListEvent
    data class Delete(val id: Long) : CategoryListEvent
    object Add : CategoryListEvent
    object Cancel : CategoryListEvent
    data class UpdatePriorities(val list: List<CategoryListItem>) : CategoryListEvent
}
