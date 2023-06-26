package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.stolexiy.catman.domain.model.DomainCategory


@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = ColorEntity::class,
            parentColumns = ["color_argb"],
            childColumns = ["category_color"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("category_color")]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "category_id") val id: Long = 0,
    @ColumnInfo(name = "category_name") val name: String,
    @ColumnInfo(name = "category_color") val color: Int,
    @ColumnInfo(name = "category_description") val description: String
) {

    fun toDomainCategory() = DomainCategory(
        id = id,
        name = name,
        color = color,
        description = description,
    )
}

fun Array<out DomainCategory>.toCategoryEntities() =
    map(DomainCategory::toCategoryEntity).toTypedArray()

fun DomainCategory.toCategoryEntity() = CategoryEntity(id, name, color, description)
