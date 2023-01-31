package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity


@Dao
abstract class CategoriesWithPurposesDao {

    @Query(
        "SELECT * FROM categories " +
                "JOIN purposes ON category_id = purpose_category_id"
    )
    abstract fun getAllWithPurposes(): Flow<Map<CategoryEntity, List<PurposeEntity>>>

    @Query(
        "SELECT * FROM categories " +
                "JOIN purposes ON category_id = purpose_category_id"
    )
    abstract suspend fun getAllWithPurposesOnce(): Map<CategoryEntity, List<PurposeEntity>>


}
