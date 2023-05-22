package ru.stolexiy.catman.ui.dialog.purpose.add

import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose

sealed interface AddPurposeEvent {
    class Add(val purpose: Purpose) : AddPurposeEvent
    object DeleteAdded : AddPurposeEvent
    object Cancel : AddPurposeEvent
}
