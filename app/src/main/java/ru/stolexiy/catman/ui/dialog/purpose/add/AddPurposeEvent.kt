package ru.stolexiy.catman.ui.dialog.purpose.add

import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.util.udf.IEvent

sealed interface AddPurposeEvent : IEvent {
    object Load : AddPurposeEvent
    class Add(val purpose: Purpose) : AddPurposeEvent
    object DeleteAdded : AddPurposeEvent
    object Cancel : AddPurposeEvent
}
