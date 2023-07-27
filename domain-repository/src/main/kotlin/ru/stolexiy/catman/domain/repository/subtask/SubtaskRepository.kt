package ru.stolexiy.catman.domain.repository.subtask

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainSubtask

interface SubtaskRepository {
    fun getAll(): Flow<List<DomainSubtask>>
    fun get(id: Long): Flow<DomainSubtask?>
    fun getAllByTask(taskId: Long): Flow<List<DomainSubtask>>
    suspend fun update(vararg subtasks: DomainSubtask): Unit
    suspend fun delete(vararg subtasks: DomainSubtask): Unit
    suspend fun insert(vararg subtasks: DomainSubtask): List<Long>
    suspend fun deleteAll(): Unit
}
