package ru.stolexiy.catman.data.datasource.local

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

object Converters {
    @TypeConverter
    fun dateToMillis(date: Date): Long = date.time

    @TypeConverter
    fun dateFromMillis(millis: Long): Date = Date(millis)
}