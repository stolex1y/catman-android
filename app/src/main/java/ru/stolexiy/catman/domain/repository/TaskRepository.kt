package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.PageRequest
import ru.stolexiy.catman.domain.model.PageResponse

interface TaskRepository {
    fun getAllTasks(): Flow<List<DomainTask>>
    fun getTask(id: Long): Flow<DomainTask?>
    fun getAllTasksByPurpose(purposeId: Long, pageRequest: PageRequest<DomainTask>): Flow<PageResponse<DomainTask>>
    fun getAllTasksByPurpose(purposeId: Long): Flow<List<DomainTask>>
    fun getAllTasksWithPurposes(pageRequest: PageRequest<DomainTask>): Flow<Map<DomainPurpose, PageResponse<DomainTask>>>
    suspend fun updateTask(vararg tasks: DomainTask)
    suspend fun deleteTask(vararg tasks: DomainTask)
    suspend fun insertTask(vararg tasks: DomainTask): List<Long>
    suspend fun deleteAllTasks()

}