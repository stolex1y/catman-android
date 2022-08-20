package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity

@androidx.room.Dao
abstract class LocalCategoryDao : Dao<CategoryEntity>() {

    @Query("SELECT * FROM categories")
    abstract override fun getAllNotDistinct(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    abstract override fun getNotDistinct(id: Long): Flow<CategoryEntity>

    @Query("SELECT * FROM categories " +
            "JOIN purposes ON categories.id = purposes.category_id")
    abstract fun getAllWithPurposes(): Flow<Map<CategoryEntity, List<PurposeEntity>>>

}