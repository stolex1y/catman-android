package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.stolexiy.catman.domain.model.DomainCategory

@Entity(
    tableName = Tables.Categories.NAME,
    foreignKeys = [
        ForeignKey(
            entity = ColorEntity::class,
            parentColumns = ["color_argb"],
            childColumns = [Tables.Categories.Fields.COLOR],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(Tables.Categories.Fields.COLOR)]
)
data class CategoryEntity(
    @ColumnInfo(name = Tables.Categories.Fields.NAME) val name: String,
    @ColumnInfo(name = Tables.Categories.Fields.COLOR) val color: Int,
    @ColumnInfo(name = Tables.Categories.Fields.DESCRIPTION) val description: String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = Tables.Categories.Fields.ID) val id: Long,
) {
    data class Response(
        @ColumnInfo(name = Tables.Categories.Fields.NAME) val name: String,
        @ColumnInfo(name = Tables.Categories.Fields.COLOR) val color: Int,
        @ColumnInfo(name = Tables.Categories.Fields.DESCRIPTION) val description: String,
        @ColumnInfo(name = Tables.Categories.Fields.PART_OF_SPENT_TIME) val partOfSpentTime: Double,
        @ColumnInfo(name = Tables.Categories.Fields.ID) val id: Long,
    ) {
        fun toDomainCategory() = DomainCategory(
            id = id,
            name = name,
            color = color,
            description = description,
            partOfSpentTime = partOfSpentTime,
        )
    }
}

fun DomainCategory.toCategoryEntity() = CategoryEntity(
    id = id,
    name = name,
    color = color,
    description = description,
)
