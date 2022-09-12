package ru.stolexiy.catman.data

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainTask
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

    override fun getTaskWithSubtasks(id: Long): Flow<DomainTask> {
        throw NotImplementedError()
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