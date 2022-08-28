package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["task_id"],
            childColumns = ["parent_task_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("parent_task_id"), Index("parent_task_id", "subtask_priority", unique = true)]
)
data class SubtaskEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "subtask_id") val id: Long,
    @ColumnInfo(name = "subtask_name") val name: String,
    @ColumnInfo(name = "subtask_description") val description: String?,
    @ColumnInfo(name = "subtask_deadline") val deadline: Calendar,
    @ColumnInfo(name = "parent_task_id") val parentTaskId: Long,
    @ColumnInfo(name = "subtask_priority") val priority: Int,
    @ColumnInfo(name = "subtask_is_finished") val isFinished: Boolean,
//    val regularity: Int?,
) {
}