package ru.stolexiy.catman.ui.categorylist

import ru.stolexiy.catman.ui.util.udfv2.IEvent

sealed interface CategoryListEvent : IEvent {
    object Load : CategoryListEvent
    data class Delete(val id: Long) : CategoryListEvent
    object Add : CategoryListEvent
    data class SwapPriority(val firstId: Long, val secondId: Long) : CategoryListEvent
}
