package ru.stolexiy.catman.data.datasource.local.db

import androidx.room.TypeConverter
import ru.stolexiy.common.DateUtils.toEpochMillis
import ru.stolexiy.common.DateUtils.toZonedDateTime
import ru.stolexiy.common.timer.Time
import java.time.ZonedDateTime

object Converters {
    @TypeConverter
    fun zonedDateTimeToMillis(zonedDateTime: ZonedDateTime): Long = zonedDateTime.toEpochMillis()

    @TypeConverter
    fun zonedDateTimeFromMillis(millis: Long): ZonedDateTime = millis.toZonedDateTime()

    @TypeConverter
    fun timeToMillis(time: Time): Long = time.inMs

    @TypeConverter
    fun timeFromMillis(millis: Long): Time = Time.from(millis)
}
