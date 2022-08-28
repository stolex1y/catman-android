package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity

/*
@androidx.room.Dao
abstract class CategoryWithPurposeDao {

@Query("SELECT * FROM categories " +
            "JOIN purposes ON categories.id = purposes.category_id")
    protected abstract fun getAllWithPurposesNotDistinct(): Flow<Map<CategoryEntity, List<PurposeEntity>>>

    fun getAllWithPurposes(): Flow<Map<CategoryEntity, List<PurposeEntity>>> =
        getAllWithPurposesNotDistinct().distinctUntilChanged()

    @Query("SELECT * FROM categories " +
            "JOIN purposes ON category_id = :categoryId")
    protected abstract fun getWithPurposesNotDistinct(categoryId: Long): Flow<Map<CategoryEntity, List<PurposeEntity>>>

    fun getWithPurposes(categoryId: Long): Flow<Map<CategoryEntity, List<PurposeEntity>>> =
        getWithPurposesNotDistinct(categoryId).distinctUntilChanged()

}
*/
