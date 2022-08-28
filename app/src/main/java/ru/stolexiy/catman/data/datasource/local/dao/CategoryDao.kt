package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity

@androidx.room.Dao
abstract class CategoryDao : Dao<CategoryEntity>() {

    @Query("SELECT * FROM categories")
    abstract override fun getAllNotDistinct(): Flow<List<CategoryEntity>>
    @Query("SELECT * FROM categories")
    abstract override fun getAllOnce(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE category_id = :id")
    abstract override fun getNotDistinct(id: Long): Flow<CategoryEntity>
    @Query("SELECT * FROM categories WHERE category_id = :id")
    abstract override fun getOnce(id: Long): CategoryEntity

    @Query("DELETE FROM categories")
    abstract override suspend fun deleteAll()

    @Query("SELECT * FROM categories " +
            "JOIN purposes ON category_id = purpose_category_id")
    protected abstract fun getAllWithPurposesNotDistinct(): Flow<Map<CategoryEntity, List<PurposeEntity>>>

    fun getAllWithPurposes(): Flow<Map<CategoryEntity, List<PurposeEntity>>> =
        getAllWithPurposesNotDistinct().distinctUntilChanged()

    @Query("SELECT * FROM categories " +
            "JOIN purposes ON category_id = purpose_category_id WHERE category_id = :categoryId")
    protected abstract fun getWithPurposesNotDistinct(categoryId: Long): Flow<Map<CategoryEntity, List<PurposeEntity>>>

    fun getWithPurposes(categoryId: Long): Flow<Map<CategoryEntity, List<PurposeEntity>>> =
        getWithPurposesNotDistinct(categoryId).distinctUntilChanged()
}