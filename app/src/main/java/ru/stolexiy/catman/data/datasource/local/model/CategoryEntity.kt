package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose


@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "category_id") val id: Long = 0,
    @ColumnInfo(name = "category_name") val name: String,
    @ColumnInfo(name = "category_color") val color: Int,
    @ColumnInfo(name = "category_description") val description: String?
) {

    fun toCategory(domainPurposes: List<DomainPurpose>? = null) = DomainCategory(id = id, name = name, color = color, description = description, domainPurposes = domainPurposes)
}

fun Array<out DomainCategory>.toCategoryEntities() = map(DomainCategory::toCategoryEntity).toTypedArray()
fun DomainCategory.toCategoryEntity() = CategoryEntity(id, name, color, description)

fun Map<CategoryEntity, List<PurposeEntity>>.toCategoriesWithPurposes(): List<DomainCategory> = map { entry ->
    entry.key.toCategory(entry.value.map(PurposeEntity::toPurpose))
}