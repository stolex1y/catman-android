package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity

@androidx.room.Dao
abstract class CategoryDao : Dao<CategoryEntity>() {

    @Query("SELECT * FROM categories")
    abstract fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE category_id = :id")
    abstract fun isCategoryExist(id: Long): Boolean

    @Query("SELECT * FROM categories WHERE category_id = :id")
    abstract fun get(id: Long): Flow<CategoryEntity?>

    @Query("DELETE FROM categories")
    abstract override suspend fun deleteAll()

    @Query(
        "SELECT * FROM categories " +
                "JOIN purposes ON category_id = purpose_category_id"
    )
    abstract fun getAllWithPurposes(): Flow<Map<CategoryEntity, List<PurposeEntity>>>
}
