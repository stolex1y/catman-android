package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate
import java.util.*

@Entity(
    tableName = "plans",
    primaryKeys = ["date", "task_id"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlanEntity(
    val date: Date,
    @ColumnInfo(name = "task_id") val taskId: Int
)