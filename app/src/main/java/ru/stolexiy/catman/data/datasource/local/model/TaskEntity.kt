package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.Date

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = PurposeEntity::class,
            parentColumns = ["id"],
            childColumns = ["purpose_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("purpose_id")]
)
data class TaskEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "purpose_id") val purposeId: Long,
    val name: String,
    val description: String?,
    val deadline: Date,
    val priority: Int,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean
//    val regularity: Int?,
) {
}