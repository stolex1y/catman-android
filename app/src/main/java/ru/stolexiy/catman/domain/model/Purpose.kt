package ru.stolexiy.catman.domain.model

import java.time.LocalDateTime
import java.util.*

data class Purpose(
    val id: Long,
    val name: String,
    val categoryId: Long,
    val deadline: Date,
    val priority: Int,
    val tasks: List<Task>?
) {
}