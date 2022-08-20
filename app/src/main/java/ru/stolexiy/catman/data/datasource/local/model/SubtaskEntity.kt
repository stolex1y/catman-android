package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDateTime
import java.util.Date

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_task_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("parent_task_id")]
)
data class SubtaskEntity(
    val id: Long,
    val name: String,
    val description: String?,
    val deadline: Date,
    @ColumnInfo(name = "parent_task_id") val parentTaskId: Long,
    val priority: Int,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean,
//    val regularity: Int?,
) {
}