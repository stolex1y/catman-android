package ru.stolexiy.common

import ru.stolexiy.common.timer.MutableTime
import ru.stolexiy.common.timer.Time
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object DateUtils {
    const val DMY_DATE = "dd.MM.yyyy"
    const val DMY_DATETIME = "dd.MM.yyyy HH:mm"
    private const val MIN_SEC_FORMAT = "%02d:%02d"

    @JvmStatic
    fun Long.toCalendar(): Calendar =
        Calendar.getInstance().apply { timeInMillis = this@toCalendar }

    @JvmStatic
    fun ZonedDateTime.isNotPast(): Boolean = this.isAfter(ZonedDateTime.now(this.zone))

    @JvmStatic
    fun ZonedDateTime.isPast(): Boolean = !isNotPast()

    @JvmStatic
    fun Time.toMinSecFormat(): String =
        MIN_SEC_FORMAT.format(this.min, this.secCeil())

    @JvmStatic
    fun ZonedDateTime?.toString(pattern: String): String {
        return if (this == null)
            ""
        else
            DateTimeFormatter.ofPattern(pattern).withLocale(Locale.getDefault())
                .format(this)
    }

    @JvmStatic
    fun Long.toZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()) =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)

    @JvmStatic
    fun Long.toZonedDateTime(otherDate: ZonedDateTime) =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), otherDate.zone)

    @JvmStatic
    fun Long.toTime() = MutableTime(this)

    @JvmStatic
    fun ZonedDateTime.toEpochMillis() = this.toInstant().toEpochMilli()

    @JvmStatic
    fun nowZonedDateTime(otherDate: ZonedDateTime) = ZonedDateTime.now(otherDate.zone)

    @JvmStatic
    fun Pair<Long, ZoneId>.toZonedDateTime() =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(first), second)

    @JvmStatic
    fun maxZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.of(999999999, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
    }

    @JvmStatic
    fun minZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.MIN, ZoneId.systemDefault())
    }

    @JvmStatic
    fun todayLastMoment(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
        ZonedDateTime.of(
            LocalDate.now(),
            LocalTime.of(23, 59, 59, 999999999),
            zone
        )

    @JvmStatic
    fun today(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
        ZonedDateTime.of(
            LocalDate.now(),
            LocalTime.of(0, 0, 0, 0),
            zone
        )

}
