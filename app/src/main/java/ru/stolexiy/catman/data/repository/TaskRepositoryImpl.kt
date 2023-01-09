package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.PageRequest
import ru.stolexiy.catman.domain.model.PageResponse
import ru.stolexiy.catman.domain.repository.TaskRepository

class TaskRepositoryImpl(
//    localDao: TaskDao
) : TaskRepository {
    override fun getAllTasks(): Flow<List<DomainTask>> {
        throw NotImplementedError()
    }

    override fun getTask(id: Long): Flow<DomainTask> {
        throw NotImplementedError()
    }

    override fun getAllTasksByPurpose(purposeId: Long): Flow<List<DomainTask>> {
        throw NotImplementedError()
    }

    override fun getAllTasksByPurpose(
        purposeId: Long,
        pageRequest: PageRequest<DomainTask>
    ): Flow<PageResponse<DomainTask>> {
        TODO("Not yet implemented")
    }

    override fun getAllTasksWithPurposes(pageRequest: PageRequest<DomainTask>): Flow<Map<DomainPurpose, PageResponse<DomainTask>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(vararg tasks: DomainTask) {
        throw NotImplementedError()
    }

    override suspend fun deleteTask(vararg tasks: DomainTask) {
        throw NotImplementedError()
    }

    override suspend fun insertTask(vararg tasks: DomainTask) {
        throw NotImplementedError()
    }

    override suspend fun deleteAllTasks() {
        throw NotImplementedError()
    }
}