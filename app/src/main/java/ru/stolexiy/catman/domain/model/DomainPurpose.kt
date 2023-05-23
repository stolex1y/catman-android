package ru.stolexiy.catman.domain.model

import ru.stolexiy.common.DateUtils.isNotPast
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class DomainPurpose(
    val name: String,
    val categoryId: Long,
    val deadline: Calendar,
    val description: String? = null,
    val isFinished: Boolean = false,
    val progress: Int = 0,
    val id: Long = 0,
    val priority: Int = 0
) {
    init {
        require(deadline.isNotPast()) { "The deadline can't be past" }
    }

    val isDeadlineBurning: Boolean
        get() {
            val difference = deadline.timeInMillis - System.currentTimeMillis()
            if (difference <= 0)
                return true
            return difference < TimeUnit.DAYS.toMillis(3)
        }
}
