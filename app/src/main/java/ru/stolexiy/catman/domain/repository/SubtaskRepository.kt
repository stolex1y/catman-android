package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainSubtask

interface SubtaskRepository {
    fun getAllSubtasks(): Flow<List<DomainSubtask>>
    fun getSubtask(id: Long): Flow<DomainSubtask>
    fun getAllSubtasksByTask(taskId: Long): Flow<List<DomainSubtask>>
    suspend fun updateSubtask(vararg subtasks: DomainSubtask)
    suspend fun deleteSubtask(vararg subtasks: DomainSubtask)
    suspend fun insertSubtask(vararg subtasks: DomainSubtask)
    suspend fun deleteAllSubtasks()
}