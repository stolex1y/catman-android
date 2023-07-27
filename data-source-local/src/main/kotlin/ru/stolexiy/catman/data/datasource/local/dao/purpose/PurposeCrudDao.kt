package ru.stolexiy.catman.data.datasource.local.dao.purpose

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.dao.DYNAMIC_PURPOSE_FIELDS
import ru.stolexiy.catman.data.datasource.local.dao.Dao
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes.Fields as purposeFields
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes.NAME as purposes

private const val GET_ALL = "SELECT *, $DYNAMIC_PURPOSE_FIELDS FROM $purposes"
private const val GET = "$GET_ALL WHERE ${purposeFields.ID} = :id"
private const val GET_ALL_BY_CATEGORY =
    "$GET_ALL WHERE ${purposeFields.CATEGORY_ID} = :categoryId"
private const val GET_ALL_BY_CATEGORY_ORDER_BY_PRIORITY =
    "$GET_ALL_BY_CATEGORY ORDER BY :sorting"
private const val DELETE_ALL = "DELETE FROM $purposes"
private const val IS_EXIST = "SELECT EXISTS($GET)"

@androidx.room.Dao
abstract class PurposeCrudDao : Dao<PurposeEntity>() {

    @Query(GET_ALL)
    abstract fun getAll(): Flow<List<PurposeEntity.Response>>

    @Query(GET)
    abstract fun get(id: Long): Flow<PurposeEntity.Response?>

    @Query(GET_ALL_BY_CATEGORY)
    abstract fun getAllByCategory(categoryId: Long): Flow<List<PurposeEntity.Response>>

    @Query(GET_ALL)
    abstract suspend fun getAllOnce(): List<PurposeEntity.Response>

    @Query(GET)
    abstract suspend fun getOnce(id: Long): PurposeEntity.Response?

    @Query(GET_ALL_BY_CATEGORY)
    abstract suspend fun getAllByCategoryOnce(categoryId: Long): List<PurposeEntity.Response>

    @Query(IS_EXIST)
    abstract suspend fun isExist(id: Long): Boolean

    @Query(DELETE_ALL)
    abstract override suspend fun deleteAll()

    @Query(GET_ALL_BY_CATEGORY_ORDER_BY_PRIORITY)
    abstract fun getAllByCategoryOrderedByPriority(
        categoryId: Long,
        sorting: String
    ): Flow<List<PurposeEntity.Response>>

    @Query(GET_ALL_BY_CATEGORY_ORDER_BY_PRIORITY)
    abstract suspend fun getAllByCategoryOrderedByPriorityOnce(
        categoryId: Long,
        sorting: String
    ): List<PurposeEntity.Response>
}
