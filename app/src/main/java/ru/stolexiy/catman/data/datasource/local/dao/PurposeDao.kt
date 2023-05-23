package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity

@androidx.room.Dao
abstract class PurposeDao : Dao<PurposeEntity>() {

    @Query("SELECT * FROM purposes")
    abstract fun getAll(): Flow<List<PurposeEntity>>

    @Query("SELECT * FROM purposes WHERE purpose_id = :id")
    abstract fun get(id: Long): Flow<PurposeEntity?>

    @Query("SELECT * FROM purposes WHERE purpose_category_id = :categoryId ORDER BY purpose_priority")
    abstract fun getAllByCategoryOrderByPriority(categoryId: Long): Flow<List<PurposeEntity>>

    @Query(
        "SELECT * FROM purposes " +
                "JOIN tasks ON task_purpose_id = purpose_id " +
                "WHERE purpose_id = :id"
    )
    abstract fun getWithTasks(id: Long): Flow<Map<PurposeEntity, List<TaskEntity>>>

    @Query("DELETE FROM purposes")
    abstract override suspend fun deleteAll()
}
