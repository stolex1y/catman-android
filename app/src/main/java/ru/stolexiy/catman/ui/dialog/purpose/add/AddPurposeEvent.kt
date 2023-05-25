package ru.stolexiy.catman.ui.dialog.purpose.add

import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.util.udfv2.IEvent

sealed interface AddPurposeEvent : IEvent {
    class Add(val purpose: Purpose) : AddPurposeEvent
    object DeleteAdded : AddPurposeEvent
    object Cancel : AddPurposeEvent
}
