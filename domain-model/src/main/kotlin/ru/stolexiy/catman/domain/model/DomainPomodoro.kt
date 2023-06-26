package ru.stolexiy.catman.domain.model

import ru.stolexiy.common.DateUtils.toTime
import ru.stolexiy.common.DateUtils.toZonedDateTime
import ru.stolexiy.common.timer.Time
import java.time.ZonedDateTime

data class DomainPomodoro(
    val dateStart: ZonedDateTime,
    val taskId: Long,
    val duration: Time
) {
    constructor(
        startDateInMillis: Long,
        taskId: Long,
        durationInMillis: Long
    ) : this(startDateInMillis.toZonedDateTime(), taskId, durationInMillis.toTime())
}
