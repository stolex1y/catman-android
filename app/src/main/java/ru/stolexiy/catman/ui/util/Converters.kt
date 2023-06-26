package ru.stolexiy.catman.ui.util

import ru.stolexiy.common.DateUtils.toString
import java.time.ZonedDateTime

object Converters {
    @JvmStatic
    fun zonedDateTimeDmyConverter(pattern: String): Converter<ZonedDateTime?, String> =
        Converter { it.toString(pattern) }
}
