package ru.stolexiy.catman.data.datasource.local.db

import androidx.room.TypeConverter
import ru.stolexiy.common.DateUtils.toEpochMillis
import ru.stolexiy.common.DateUtils.toZonedDateTime
import java.time.ZonedDateTime

object Converters {
    @TypeConverter
    fun calendarToMillis(zonedDateTime: ZonedDateTime): Long = zonedDateTime.toEpochMillis()

    @TypeConverter
    fun calendarFromMillis(millis: Long): ZonedDateTime = millis.toZonedDateTime()
}