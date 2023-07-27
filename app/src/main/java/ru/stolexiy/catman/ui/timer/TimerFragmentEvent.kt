package ru.stolexiy.catman.ui.timer

import ru.stolexiy.catman.ui.util.udf.IEvent

sealed interface TimerFragmentEvent : IEvent {
    object Load : TimerFragmentEvent
    object Start : TimerFragmentEvent
    object Stop : TimerFragmentEvent
    object Pause : TimerFragmentEvent
}
