package ru.stolexiy.catman.data.datasource.local.dao.task

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.dao.Dao
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables.Tasks.Fields as field
import ru.stolexiy.catman.data.datasource.local.model.Tables.Tasks.NAME as table

private const val GET_ALL = "SELECT * FROM $table"
private const val GET_BY_ID = "$GET_ALL WHERE ${field.ID} = :id"
private const val DELETE_ALL = "DELETE FROM $table"
private const val GET_ALL_BY_PURPOSE =
    "$GET_ALL WHERE ${field.PURPOSE_ID} = :purposeId"
private const val GET_ALL_BY_PURPOSE_ORDER_BY_PRIORITY =
    "$GET_ALL_BY_PURPOSE ORDER BY ${field.PRIORITY}"

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
    abstract override suspend fun deleteAll()

    @Query(GET_ALL_BY_PURPOSE)
    abstract fun getAllByPurpose(
        purposeId: Long
    ): Flow<List<TaskEntity>>

    @Query(GET_ALL_BY_PURPOSE)
    abstract suspend fun getAllByPurposeOnce(
        purposeId: Long
    ): List<TaskEntity>

    @Query(GET_ALL_BY_PURPOSE_ORDER_BY_PRIORITY)
    abstract fun getAllByPurposeOrderedByPriority(
        purposeId: Long,
//        direction: String
    ): Flow<List<TaskEntity>>

    @Query(GET_ALL_BY_PURPOSE_ORDER_BY_PRIORITY)
    abstract suspend fun getAllByPurposeOrderedByPriorityOnce(
        purposeId: Long,
//        direction: String
    ): List<TaskEntity>
}
