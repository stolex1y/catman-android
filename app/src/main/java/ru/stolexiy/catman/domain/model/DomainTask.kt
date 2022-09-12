package ru.stolexiy.catman.domain.model

import java.util.*

data class DomainTask(
    val purposeId: Long,
    val name: String,
    val deadline: Calendar,
    val description: String? = null,
    val isFinished: Boolean = false,
    val progress: Int = 0,
    val priority: Int = 0,
    val id: Long = 0,
    val subtasks: List<DomainSubtask>? = null
) {
}