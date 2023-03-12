package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.*
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import ru.stolexiy.catman.domain.model.DomainPurpose
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
    indices = [Index("purpose_category_id"), Index("purpose_category_id", "purpose_priority")]
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
) {
    fun toDomainPurpose() = DomainPurpose(
        id = id,
        name = name,
        categoryId = categoryId,
        deadline = deadline,
        priority = priority,
        isFinished = isFinished,
        description = description,
        progress = progress
    )
}

fun DomainPurpose.toPurposeEntity() = PurposeEntity(
    id = id,
    name = name,
    categoryId = categoryId,
    deadline = deadline,
    priority = priority,
    isFinished = isFinished,
    description = description,
    progress = progress
)

fun Array<out DomainPurpose>.toPurposeEntities() =
    map(DomainPurpose::toPurposeEntity).toTypedArray()

