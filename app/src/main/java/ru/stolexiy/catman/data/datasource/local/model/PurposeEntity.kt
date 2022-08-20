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
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [Index("category_id")]
)
data class PurposeEntity(
    @PrimaryKey val id: Long,
    val name: String,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    val deadline: Date,
    val priority: Int,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean
)