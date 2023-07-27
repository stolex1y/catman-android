package ru.stolexiy.catman.domain.model

import ru.stolexiy.common.timer.Time
import java.time.ZonedDateTime
import kotlin.math.min

data class DomainTask(
    val purposeId: Long,
    val name: String,
    val startTime: ZonedDateTime? = null,
    val deadline: ZonedDateTime,
    val description: String = "",
    val isFinished: Boolean = false,
    val priority: Int,
    val id: Long = 0,
    val planTimeCosts: Time? = null,
    val completionTime: ZonedDateTime? = null,
    val regularityId: Long? = null,
    val actualTimeCosts: Time,
) {
    val progress: Double = if (isFinished)
        1.0
    else if (planTimeCosts == null)
        0.0
    else {
        val progress = actualTimeCosts.inMs / planTimeCosts.inMs.toDouble()
        min(progress, 1.0)
    }
}
