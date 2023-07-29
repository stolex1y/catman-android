package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.stolexiy.catman.domain.model.DomainPurpose
import java.time.ZonedDateTime

@Entity(
    tableName = Tables.Purposes.NAME,
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = [Tables.Categories.Fields.ID],
            childColumns = [Tables.Purposes.Fields.CATEGORY_ID],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(Tables.Purposes.Fields.CATEGORY_ID),
        Index(Tables.Purposes.Fields.ID, Tables.Purposes.Fields.PRIORITY)
    ]
)
data class PurposeEntity(
    @ColumnInfo(name = Tables.Purposes.Fields.NAME) val name: String,
    @ColumnInfo(name = Tables.Purposes.Fields.CATEGORY_ID) val categoryId: Long,
    @ColumnInfo(name = Tables.Purposes.Fields.DEADLINE) val deadline: ZonedDateTime,
    @ColumnInfo(name = Tables.Purposes.Fields.DESCRIPTION) val description: String,
    @ColumnInfo(name = Tables.Purposes.Fields.PRIORITY) val priority: Int,
    @ColumnInfo(name = Tables.Purposes.Fields.IS_FINISHED) val isFinished: Boolean,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = Tables.Purposes.Fields.ID) val id: Long,
) {
    data class Response(
        @ColumnInfo(name = Tables.Purposes.Fields.NAME) val name: String,
        @ColumnInfo(name = Tables.Purposes.Fields.CATEGORY_ID) val categoryId: Long,
        @ColumnInfo(name = Tables.Purposes.Fields.DEADLINE) val deadline: ZonedDateTime,
        @ColumnInfo(name = Tables.Purposes.Fields.DESCRIPTION) val description: String,
        @ColumnInfo(name = Tables.Purposes.Fields.PRIORITY) val priority: Int,
        @ColumnInfo(name = Tables.Purposes.Fields.IS_FINISHED) val isFinished: Boolean,
        @ColumnInfo(name = Tables.Purposes.Fields.PROGRESS) val progress: Double,
        @ColumnInfo(name = Tables.Purposes.Fields.ID) val id: Long,
    ) {
        fun toDomainPurpose() = DomainPurpose(
            id = id,
            name = name,
            categoryId = categoryId,
            deadline = deadline,
            priority = priority,
            isFinished = isFinished,
            description = description,
            progress = progress,
        )
    }
}

fun DomainPurpose.toPurposeEntity() = PurposeEntity(
    id = id,
    name = name,
    categoryId = categoryId,
    deadline = deadline,
    priority = priority,
    isFinished = isFinished,
    description = description,
)
