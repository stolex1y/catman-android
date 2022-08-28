package ru.stolexiy.catman.domain.model

import androidx.room.ColumnInfo
import java.util.*

data class Subtask(
    val name: String,
    val deadline: Calendar,
    val parentTaskId: Long,
    val description: String? = null,
    val isFinished: Boolean = false,
    val progress: Int = 0,
    val priority: Int = 0,
    val id: Long = 0,
)
