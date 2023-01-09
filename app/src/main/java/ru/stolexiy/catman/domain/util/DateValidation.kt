package ru.stolexiy.catman.domain.util

import java.util.Calendar

object DateValidation {
    private val today: Calendar = Calendar.getInstance().apply {
        set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), 0, 0, 0)
    }
    fun isNotPastDeadline(calendar: Calendar) = calendar.timeInMillis >= today.timeInMillis
}
