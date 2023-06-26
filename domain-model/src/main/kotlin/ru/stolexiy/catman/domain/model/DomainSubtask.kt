package ru.stolexiy.catman.domain.model

import ru.stolexiy.common.DateUtils.toZonedDateTime
import java.time.ZonedDateTime

data class DomainSubtask(
    val name: String,
    val parentTaskId: Long,
    val deadline: ZonedDateTime? = null,
    val description: String = "",
    val isFinished: Boolean = false,
    val progress: Int = 0,
    val priority: Int = 0,
    val id: Long = 0,
) {
    constructor(
        name: String,
        parentTaskId: Long,
        deadlineInMillis: Long,
        description: String = "",
        isFinished: Boolean = false,
        progress: Int = 0,
        priority: Int = 0,
        id: Long = 0,
    ) : this(
        name,
        parentTaskId,
        deadlineInMillis.toZonedDateTime(),
        description,
        isFinished,
        progress,
        priority,
        id
    )
}
