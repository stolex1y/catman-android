package ru.stolexiy.common

import ru.stolexiy.common.timer.ImmutableTime
import java.util.Calendar

object DateUtils {
    private const val MIN_SEC_FORMAT = "%02d:%02d"

    @JvmStatic
    fun todayCalendar(): Calendar = Calendar.getInstance().apply {
        set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), 0, 0, 0)
    }

    @JvmStatic
    fun Long.toCalendar(): Calendar =
        Calendar.getInstance().apply { timeInMillis = this@toCalendar }

    @JvmStatic
    fun Calendar.isNotPast(): Boolean = this.timeInMillis >= todayCalendar().timeInMillis

    @JvmStatic
    fun ImmutableTime.toMinSecFormat(): String =
        MIN_SEC_FORMAT.format(this.min, this.secCeil())
}
