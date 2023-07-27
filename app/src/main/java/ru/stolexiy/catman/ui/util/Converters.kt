package ru.stolexiy.catman.ui.util

import ru.stolexiy.common.DateUtils.toHMinFormat
import ru.stolexiy.common.DateUtils.toString
import ru.stolexiy.common.timer.Time
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

object Converters {
    @JvmStatic
    fun zonedDateTimeConverter(pattern: String): ToTextConverter<ZonedDateTime?> =
        ToTextConverter { it.toString(pattern) }

    @JvmStatic
    fun localDateConverter(pattern: String): ToTextConverter<LocalDate?> =
        ToTextConverter { it.toString(pattern) }

    @JvmStatic
    fun localTimeConverter(pattern: String): ToTextConverter<LocalTime?> =
        ToTextConverter { it.toString(pattern) }

    @JvmStatic
    fun hmFormatTimeConverter(): ToTextConverter<Time?> =
        ToTextConverter { it?.toHMinFormat() ?: "" }

    @JvmStatic
    fun intConverter(): ToTextConverter<Int?> =
        ToTextConverter { it?.toString() ?: "" }
}
