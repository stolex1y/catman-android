package ru.stolexiy.catman.data.datasource.local.dao.category

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.dao.DYNAMIC_CATEGORY_FIELDS
import ru.stolexiy.catman.data.datasource.local.dao.DYNAMIC_PURPOSE_FIELDS
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables.Categories.Fields as categoryFields
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes.Fields as purposeFields
import ru.stolexiy.catman.data.datasource.local.model.Tables.Categories.NAME as categories
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes.NAME as purposes

private const val GET_ALL_WITH_PURPOSES = "SELECT *, " +
        "$DYNAMIC_CATEGORY_FIELDS, " +
        "$DYNAMIC_PURPOSE_FIELDS " +
        "FROM $categories " +
        "LEFT JOIN $purposes ON ${categoryFields.ID} = ${purposeFields.CATEGORY_ID}"

@Dao
abstract class CategoryWithPurposesDao {

    @Query(GET_ALL_WITH_PURPOSES)
    abstract fun getAll(): Flow<Map<CategoryEntity.Response, List<PurposeEntity.Response>>>

    @Query(GET_ALL_WITH_PURPOSES)
    abstract suspend fun getAllOnce(): Map<CategoryEntity.Response, List<PurposeEntity.Response>>
}
