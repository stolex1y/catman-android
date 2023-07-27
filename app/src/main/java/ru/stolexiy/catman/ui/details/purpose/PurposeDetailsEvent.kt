package ru.stolexiy.catman.ui.details.purpose

import ru.stolexiy.catman.ui.util.udf.IEvent

sealed interface PurposeDetailsEvent : IEvent {
    data class SwapPriority(val firstId: Long, val secondId: Long) : PurposeDetailsEvent
    object Cancel : PurposeDetailsEvent
    data class MarkFinished(val taskId: Long) : PurposeDetailsEvent
    data class MarkNotFinished(val taskId: Long) : PurposeDetailsEvent
}
