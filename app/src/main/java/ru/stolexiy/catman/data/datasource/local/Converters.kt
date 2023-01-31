package ru.stolexiy.catman.data.datasource.local

import androidx.room.TypeConverter
import ru.stolexiy.catman.domain.util.DateUtils.toCalendar
import java.util.Calendar

object Converters {
    @TypeConverter
    fun calendarToMillis(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun calendarFromMillis(millis: Long): Calendar = millis.toCalendar()
}