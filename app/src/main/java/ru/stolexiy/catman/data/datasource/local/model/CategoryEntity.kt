package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Long = 0,
    val name: String
) {
}