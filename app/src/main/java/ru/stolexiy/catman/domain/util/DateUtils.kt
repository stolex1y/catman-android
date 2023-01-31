package ru.stolexiy.catman.domain.util

import java.util.Calendar

object DateUtils {
    fun todayCalendar(): Calendar = Calendar.getInstance().apply {
        set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), 0, 0, 0)
    }

    fun Long.toCalendar(): Calendar = Calendar.getInstance().apply { timeInMillis = this@toCalendar }

    fun Calendar.isNotPast(): Boolean = this.timeInMillis >= todayCalendar().timeInMillis
}