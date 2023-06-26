package ru.stolexiy.catman.domain.model

import ru.stolexiy.common.DateUtils.toZonedDateTime
import java.time.ZonedDateTime

data class DomainPurpose(
    val name: String,
    val categoryId: Long,
    val deadline: ZonedDateTime,
    val description: String = "",
    val isFinished: Boolean = false,
    val progress: Int = 0,
    val id: Long = 0,
    val priority: Int = 0
) {
    constructor(
        name: String,
        categoryId: Long,
        deadlineInMillis: Long,
        description: String = "",
        isFinished: Boolean = false,
        progress: Int = 0,
        id: Long = 0,
        priority: Int = 0
    ) : this(
        name,
        categoryId,
        deadlineInMillis.toZonedDateTime(),
        description,
        isFinished,
        progress,
        id,
        priority
    )

    val isDeadlineBurning: Boolean
        get() {
            return deadline.isBefore(ZonedDateTime.now(deadline.zone).plusDays(3))
        }
}
