package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainTask

interface TaskRepository {
    fun getAllTasks(): Flow<List<DomainTask>>
    fun getTask(id: Long): Flow<DomainTask>
    fun getAllTasksByPurpose(purposeId: Long): Flow<List<DomainTask>>
    fun getTaskWithSubtasks(id: Long): Flow<DomainTask>
    suspend fun updateTask(vararg tasks: DomainTask)
    suspend fun deleteTask(vararg tasks: DomainTask)
    suspend fun insertTask(vararg tasks: DomainTask)
    suspend fun deleteAllTasks()
}