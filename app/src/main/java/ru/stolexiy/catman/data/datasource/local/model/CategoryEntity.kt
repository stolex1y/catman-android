package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "category_id") val id: Long = 0,
    @ColumnInfo(name = "category_name") val name: String,
    @ColumnInfo(name = "category_color") val color: Int,
    @ColumnInfo(name = "category_description") val description: String?
) {
}