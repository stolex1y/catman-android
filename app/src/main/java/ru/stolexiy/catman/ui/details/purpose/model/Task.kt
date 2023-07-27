package ru.stolexiy.catman.ui.details.purpose.model

import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.common.timer.Time
import java.time.ZonedDateTime

data class Task(
    val name: String,
    val completionTime: ZonedDateTime?,
    val deadline: ZonedDateTime,
    val isFinished: Boolean,
    val planTimeCosts: Time?,
    val actualTimeCosts: Time,
    val priority: Int,
    val id: Long,
) {
    constructor() : this(
        name = "",
        completionTime = null,
        deadline = ZonedDateTime.now(),
        isFinished = false,
        planTimeCosts = Time.ZERO,
        actualTimeCosts = Time.ZERO,
        priority = 0,
        id = 0,
    )
}

fun DomainTask.toTask() = Task(
    name = name,
    deadline = deadline,
    isFinished = isFinished,
    completionTime = completionTime,
    planTimeCosts = planTimeCosts,
    actualTimeCosts = actualTimeCosts,
    priority = priority,
    id = id,
)
