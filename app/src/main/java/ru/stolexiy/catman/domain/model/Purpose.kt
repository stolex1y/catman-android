package ru.stolexiy.catman.domain.model

import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.temporal.TemporalAccessor
import java.util.*
import java.util.concurrent.TimeUnit

data class Purpose(
    val name: String,
    val categoryId: Long,
    val deadline: Calendar,
    val description: String? = null,
    val isFinished: Boolean = false,
    val progress: Int = 0,
    val id: Long = 0,
    val priority: Int = 0,
    val tasks: List<Task>? = null
) {
    val isDeadlineBurning: Boolean
        get() {
            val difference = deadline.timeInMillis - System.currentTimeMillis()
            if (difference <= 0)
                return true
            return difference < TimeUnit.DAYS.toMillis(3)
        }
}