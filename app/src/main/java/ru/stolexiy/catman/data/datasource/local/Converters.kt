package ru.stolexiy.catman.data.datasource.local

import android.graphics.Color
import android.graphics.ColorSpace
import androidx.room.TypeConverter
import java.util.*

object Converters {
    @TypeConverter
    fun calendarToMillis(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun calendarFromMillis(millis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = millis }
}