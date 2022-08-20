package ru.stolexiy.catman.domain.model

import java.util.*

data class Task(
    val id: Long,
    val purposeId: Long,
    val name: String,
    val description: String?,
    val deadline: Date?,
    val parentTask: Task?,
    val priority: Int,
    val subtasks: List<Task>?
) {
}