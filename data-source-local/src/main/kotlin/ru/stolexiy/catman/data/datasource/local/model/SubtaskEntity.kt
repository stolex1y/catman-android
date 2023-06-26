package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["task_id"],
            childColumns = ["subtask_parent_task_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("subtask_parent_task_id"), Index("subtask_parent_task_id", "subtask_priority")]
)
data class SubtaskEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "subtask_id") val id: Long,
    @ColumnInfo(name = "subtask_name") val name: String,
    @ColumnInfo(name = "subtask_description") val description: String,
    @ColumnInfo(name = "subtask_deadline") val deadline: ZonedDateTime?,
    @ColumnInfo(name = "subtask_parent_task_id") val parentTaskId: Long,
    @ColumnInfo(name = "subtask_priority") val priority: Int,
    @ColumnInfo(name = "subtask_is_finished") val isFinished: Boolean,
//    val regularity: Int?,
) {
}