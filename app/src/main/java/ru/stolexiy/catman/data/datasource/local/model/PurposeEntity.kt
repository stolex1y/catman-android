package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.time.LocalDateTime
import java.util.*

@Entity(
    tableName = "purposes",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["purpose_category_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [Index("purpose_category_id"), Index("purpose_category_id", "purpose_priority", unique = true)]
)
data class PurposeEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "purpose_id") val id: Long,
    @ColumnInfo(name = "purpose_name") val name: String,
    @ColumnInfo(name = "purpose_category_id") val categoryId: Long,
    @ColumnInfo(name = "purpose_deadline") val deadline: Calendar,
    @ColumnInfo(name = "purpose_description") val description: String?,
    @ColumnInfo(name = "purpose_priority") val priority: Int,
    @ColumnInfo(name = "purpose_is_finished") val isFinished: Boolean,
    @ColumnInfo(name = "purpose_progress") val progress: Int,

)