package ru.stolexiy.catman.data.datasource.local.dao.category

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.dao.DYNAMIC_CATEGORY_FIELDS
import ru.stolexiy.catman.data.datasource.local.dao.Dao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables.Categories.Fields as categoryFields
import ru.stolexiy.catman.data.datasource.local.model.Tables.Categories.NAME as categories

private const val GET_ALL =
    "SELECT *, $DYNAMIC_CATEGORY_FIELDS FROM $categories"
private const val GET = "$GET_ALL WHERE ${categoryFields.ID} = :id"
private const val IS_EXIST = "SELECT EXISTS($GET)"
private const val DELETE_ALL = "DELETE FROM $categories"

@androidx.room.Dao
abstract class CategoryCrudDao : Dao<CategoryEntity>() {

    @Query(GET_ALL)
    abstract fun getAll(): Flow<List<CategoryEntity.Response>>

    @Query(GET_ALL)
    abstract suspend fun getAllOnce(): List<CategoryEntity.Response>

    @Query(IS_EXIST)
    abstract suspend fun isCategoryExist(id: Long): Boolean

    @Query(GET)
    abstract fun get(id: Long): Flow<CategoryEntity.Response?>

    @Query(GET)
    abstract suspend fun getOnce(id: Long): CategoryEntity.Response?

    @Query(DELETE_ALL)
    abstract override suspend fun deleteAll()
}
