package ru.stolexiy.catman.data.datasource.local.model

import ru.stolexiy.catman.core.DayOfTheWeek

sealed class Regularity {
    class Period(val period: Int)
    class Weekdays(vararg _days: DayOfTheWeek) {
        val days = setOf(_days)
    }
}
