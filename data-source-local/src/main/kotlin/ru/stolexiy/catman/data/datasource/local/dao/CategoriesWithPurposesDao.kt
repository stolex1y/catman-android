package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity

private const val GET_ALL_WITH_PURPOSE = "SELECT * FROM categories " +
        "LEFT JOIN purposes ON category_id = purpose_category_id"

@Dao
abstract class CategoriesWithPurposesDao {

    @Query(GET_ALL_WITH_PURPOSE)
    abstract fun getAllWithPurposes(): Flow<Map<CategoryEntity, List<PurposeEntity>>>

    @Query(GET_ALL_WITH_PURPOSE)
    abstract suspend fun getAllWithPurposesOnce(): Map<CategoryEntity, List<PurposeEntity>>

}
