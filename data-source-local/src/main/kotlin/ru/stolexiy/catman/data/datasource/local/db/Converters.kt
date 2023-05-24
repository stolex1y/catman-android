package ru.stolexiy.catman.data.datasource.local.db

import androidx.room.TypeConverter
import ru.stolexiy.common.DateUtils.toCalendar
import java.util.Calendar

object Converters {
    @TypeConverter
    fun calendarToMillis(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun calendarFromMillis(millis: Long): Calendar = millis.toCalendar()
}