package ru.stolexiy.catman.domain.model

import ru.stolexiy.common.DateUtils.toZonedDateTime
import ru.stolexiy.common.timer.Time
import java.time.ZonedDateTime

data class DomainTask(
    val purposeId: Long,
    val name: String,
    val deadline: ZonedDateTime? = null,
    val description: String = "",
    val isFinished: Boolean = false,
    val progress: Int = 0,
    val priority: Int,
    val id: Long = 0,
    val timeCosts: Time = Time.ZERO
) {
    constructor(
        purposeId: Long,
        name: String,
        deadlineInMillis: Long,
        description: String = "",
        isFinished: Boolean = false,
        progress: Int = 0,
        priority: Int = 0,
        id: Long = 0,
        timeCosts: Time = Time.ZERO
    ) : this(
        purposeId,
        name,
        deadlineInMillis.toZonedDateTime(),
        description,
        isFinished,
        progress,
        priority,
        id,
        timeCosts
    )
}
