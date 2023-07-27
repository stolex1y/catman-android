package ru.stolexiy.catman.domain.model

import java.time.ZonedDateTime

data class DomainPurpose(
    val name: String,
    val categoryId: Long,
    val deadline: ZonedDateTime,
    val description: String,
    val isFinished: Boolean,
    val id: Long,
    val priority: Int,
    val progress: Double,
) {

    val isDeadlineBurning: Boolean =
        deadline.isBefore(ZonedDateTime.now(deadline.zone).plusDays(3))
}
