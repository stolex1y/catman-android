package ru.stolexiy.catman.data.datasource.local.dao.task

import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.dao.Dao
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables.Tasks.Fields as field
import ru.stolexiy.catman.data.datasource.local.model.Tables.Tasks.NAME as table

private const val GET_ALL = "SELECT * FROM $table"
private const val GET_BY_ID = "$GET_ALL WHERE ${field.ID} = :id"
private const val DELETE_ALL = "DELETE FROM $table"
private const val GET_ALL_BY_PURPOSE =
    "$GET_ALL WHERE ${field.PURPOSE_ID} = :purposeId"

@androidx.room.Dao
abstract class TaskCrudDao : Dao<TaskEntity>() {

    @Query(GET_ALL)
    abstract fun getAll(): Flow<List<TaskEntity>>

    @Query(GET_ALL)
    abstract suspend fun getAllOnce(): List<TaskEntity>

    @Query(GET_BY_ID)
    abstract fun get(id: Long): Flow<TaskEntity?>

    @Query(GET_BY_ID)
    abstract suspend fun getOnce(id: Long): TaskEntity?

    @Query(DELETE_ALL)
    abstract override suspend fun deleteAll(): Int

    @Query(GET_ALL_BY_PURPOSE)
    abstract fun getAllByPurpose(
        purposeId: Long
    ): Flow<List<TaskEntity>>

    @Query(GET_ALL_BY_PURPOSE)
    abstract suspend fun getAllByPurposeOnce(
        purposeId: Long
    ): List<TaskEntity>

    @RawQuery(observedEntities = [TaskEntity::class, PurposeEntity::class])
    internal abstract fun getAllByPurposeOrdered(
        query: SupportSQLiteQuery
    ): Flow<List<TaskEntity>>

    @RawQuery(observedEntities = [TaskEntity::class, PurposeEntity::class])
    internal abstract suspend fun getAllByPurposeOnceOrdered(
        query: SupportSQLiteQuery
    ): List<TaskEntity>

    fun getAllByPurposeOrdered(
        purposeId: Long,
        sorting: String
    ): Flow<List<TaskEntity>> {
        return getAllByPurposeOrdered(
            getAllByCategoryOrderedQuery(purposeId, sorting)
        )
    }

    suspend fun getAllByPurposeOnceOrdered(
        purposeId: Long,
        sorting: String
    ): List<TaskEntity> {
        return getAllByPurposeOnceOrdered(
            getAllByCategoryOrderedQuery(purposeId, sorting)
        )
    }

    companion object {
        private fun getAllByCategoryOrderedQuery(
            purposeId: Long,
            sorting: String
        ): SupportSQLiteQuery {
            val query =
                "$GET_ALL WHERE ${field.PURPOSE_ID} = $purposeId ORDER BY $sorting"
            return SimpleSQLiteQuery(query, arrayOf())
        }
    }
}
