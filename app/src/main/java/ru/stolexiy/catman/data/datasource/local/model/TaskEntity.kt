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
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = PurposeEntity::class,
            parentColumns = ["purpose_id"],
            childColumns = ["task_purpose_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("task_purpose_id"), Index("task_purpose_id", "task_priority", unique = true)]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "task_id") val id: Long,
    @ColumnInfo(name = "task_purpose_id") val purposeId: Long,
    @ColumnInfo(name = "task_name") val name: String,
    @ColumnInfo(name = "task_description") val description: String?,
    @ColumnInfo(name = "task_deadline") val deadline: Calendar,
    @ColumnInfo(name = "task_priority") val priority: Int,
    @ColumnInfo(name = "task_is_finished") val isFinished: Boolean,
    @ColumnInfo(name = "task_progress") val progress: Int,
//    val regularity: Int?,
) {
}