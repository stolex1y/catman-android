package ru.stolexiy.catman.ui.dialog.purpose.add

import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.util.udf.IEvent

sealed interface AddPurposeDialogEvent : IEvent {
    object Load : AddPurposeDialogEvent
    data class Add(val purpose: Purpose) : AddPurposeDialogEvent
    object DeleteAdded : AddPurposeDialogEvent
    object Cancel : AddPurposeDialogEvent
}
