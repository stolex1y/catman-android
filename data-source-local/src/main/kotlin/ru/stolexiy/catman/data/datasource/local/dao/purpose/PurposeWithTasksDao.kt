package ru.stolexiy.catman.data.datasource.local.dao.purpose

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.dao.DYNAMIC_PURPOSE_FIELDS
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes.Fields as purposeFields
import ru.stolexiy.catman.data.datasource.local.model.Tables.Purposes.NAME as purposes
import ru.stolexiy.catman.data.datasource.local.model.Tables.Tasks.NAME as tasks
import ru.stolexiy.catman.data.datasource.local.model.Tables.Tasks.Fields as taskFields


private const val GET_ALL_WITH_TASKS =
    "SELECT *, $DYNAMIC_PURPOSE_FIELDS FROM $purposes JOIN $tasks ON ${taskFields.PURPOSE_ID} = ${purposeFields.ID}"
private const val GET_BY_ID_WITH_TASKS =
    "$GET_ALL_WITH_TASKS WHERE ${purposeFields.ID} = :purposeId"

@Dao
abstract class PurposeWithTasksDao {
    @Query(GET_ALL_WITH_TASKS)
    abstract fun getAllWithTasks(): Flow<Map<PurposeEntity.Response, List<TaskEntity>>>

    @Query(GET_ALL_WITH_TASKS)
    abstract suspend fun getAllWithTasksOnce(): Map<PurposeEntity.Response, List<TaskEntity>>

    @Query(GET_BY_ID_WITH_TASKS)
    abstract fun getByIdWithTasks(purposeId: Long): Flow<Map<PurposeEntity.Response, List<TaskEntity>>>

    @Query(GET_BY_ID_WITH_TASKS)
    abstract suspend fun getByIdWithTasksOnce(purposeId: Long): Map<PurposeEntity.Response, List<TaskEntity>>
}
