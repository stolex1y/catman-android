package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.ZonedDateTime

@Entity(
    tableName = "plans",
    primaryKeys = ["plan_date", "plan_task_id"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = [Tables.Tasks.Fields.ID],
            childColumns = ["plan_task_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plan_task_id")]
)
data class PlanEntity(
    @ColumnInfo(name = "plan_date") val date: ZonedDateTime,
    @ColumnInfo(name = "plan_task_id") val taskId: Long
)