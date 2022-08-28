package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity

@androidx.room.Dao
abstract class PurposeDao : Dao<PurposeEntity>() {

    @Query("SELECT * FROM purposes")
    abstract override fun getAllNotDistinct(): Flow<List<PurposeEntity>>
    @Query("SELECT * FROM purposes")
    abstract override fun getAllOnce(): List<PurposeEntity>

    @Query("SELECT * FROM purposes WHERE purpose_id = :id")
    abstract override fun getNotDistinct(id: Long): Flow<PurposeEntity>
    @Query("SELECT * FROM purposes WHERE purpose_id = :id")
    abstract override fun getOnce(id: Long): PurposeEntity

    @Query("SELECT * FROM purposes WHERE purpose_category_id = :categoryId ORDER BY purpose_priority")
    protected abstract fun getAllByCategoryOrderByPriorityNotDistinct(categoryId: Long): Flow<List<PurposeEntity>>

    fun getAllByCategoryOrderByPriority(categoryId: Long): Flow<List<PurposeEntity>> =
        getAllByCategoryOrderByPriorityNotDistinct(categoryId).distinctUntilChanged()

    @Query("SELECT * FROM purposes WHERE purpose_category_id = :categoryId ORDER BY purpose_priority")
    abstract fun getAllByCategoryOrderByPriorityOnce(categoryId: Long): List<PurposeEntity>

    @Query("SELECT * FROM purposes " +
            "JOIN tasks ON task_purpose_id = purpose_id " +
            "WHERE purpose_id = :id")
    protected abstract fun getWithTasksNotDistinct(id: Long): Flow<Map<PurposeEntity, List<TaskEntity>>>

    fun getWithTasks(id: Long): Flow<Map<PurposeEntity, List<TaskEntity>>> =
        getWithTasksNotDistinct(id).distinctUntilChanged()

    @Query("DELETE FROM purposes")
    abstract override suspend fun deleteAll()
}