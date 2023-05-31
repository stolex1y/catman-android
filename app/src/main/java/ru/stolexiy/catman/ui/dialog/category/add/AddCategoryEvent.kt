package ru.stolexiy.catman.ui.dialog.category.add

import ru.stolexiy.catman.ui.dialog.category.model.Category
import ru.stolexiy.catman.ui.util.udfv2.IEvent

sealed interface AddCategoryEvent : IEvent {
    object Load : AddCategoryEvent
    class Add(val category: Category) : AddCategoryEvent
    object DeleteAdded : AddCategoryEvent
    object Cancel : AddCategoryEvent
}
