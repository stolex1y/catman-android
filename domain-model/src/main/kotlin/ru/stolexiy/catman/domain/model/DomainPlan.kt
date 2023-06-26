package ru.stolexiy.catman.domain.model

import ru.stolexiy.common.DateUtils.toZonedDateTime
import java.time.ZonedDateTime

data class DomainPlan(
    val date: ZonedDateTime,
    val tasks: List<Long> = listOf()
) {
    constructor(
        dateInMillis: Long,
        tasks: List<Long> = listOf()
    ) : this(dateInMillis.toZonedDateTime(), tasks)
}
