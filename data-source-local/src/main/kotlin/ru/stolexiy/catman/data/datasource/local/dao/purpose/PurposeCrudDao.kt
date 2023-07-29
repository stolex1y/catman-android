package ru.stolexiy.catman.data.datasource.local.dao.purpose

import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.dao.DYNAMIC_PURPOSE_FIELDS
import ru.stolexiy.catman.data.datasource.local.dao.Dao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes.Fields as purposeFields
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes.NAME as purposes

private const val GET_ALL = "SELECT *, $DYNAMIC_PURPOSE_FIELDS FROM $purposes"
private const val GET = "$GET_ALL WHERE ${purposeFields.ID} = :id"
private const val GET_ALL_BY_CATEGORY =
    "$GET_ALL WHERE ${purposeFields.CATEGORY_ID} = :categoryId"
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
    abstract override suspend fun deleteAll(): Int

    @RawQuery(observedEntities = [PurposeEntity::class, CategoryEntity::class, TaskEntity::class])
    internal abstract fun getAllByCategoryOrdered(
        query: SupportSQLiteQuery
    ): Flow<List<PurposeEntity.Response>>

    @RawQuery(observedEntities = [PurposeEntity::class, CategoryEntity::class, TaskEntity::class])
    internal abstract suspend fun getAllByCategoryOnceOrdered(
        query: SupportSQLiteQuery
    ): List<PurposeEntity.Response>

    fun getAllByCategoryOrdered(
        categoryId: Long,
        sorting: String
    ): Flow<List<PurposeEntity.Response>> {
        return getAllByCategoryOrdered(
            getAllByCategoryOrderedQuery(categoryId, sorting)
        )
    }

    suspend fun getAllByCategoryOnceOrdered(
        categoryId: Long,
        sorting: String
    ): List<PurposeEntity.Response> {
        return getAllByCategoryOnceOrdered(
            getAllByCategoryOrderedQuery(categoryId, sorting)
        )
    }

    companion object {
        private fun getAllByCategoryOrderedQuery(
            categoryId: Long,
            sorting: String
        ): SupportSQLiteQuery {
            val query =
                "$GET_ALL WHERE ${purposeFields.CATEGORY_ID} = $categoryId ORDER BY $sorting"
            return SimpleSQLiteQuery(query, arrayOf())
        }
    }
}
