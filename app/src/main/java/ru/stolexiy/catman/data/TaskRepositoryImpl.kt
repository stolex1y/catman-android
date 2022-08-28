package ru.stolexiy.catman.data

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Task
import ru.stolexiy.catman.domain.repository.TaskRepository

class TaskRepositoryImpl(
//    localDao: TaskDao
) : TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> {
        throw NotImplementedError()
    }

    override fun getTask(id: Long): Flow<Task> {
        throw NotImplementedError()
    }

    override fun getAllTasksByPurpose(purposeId: Long): Flow<List<Task>> {
        throw NotImplementedError()
    }

    override fun getTaskWithSubtasks(id: Long): Flow<Task> {
        throw NotImplementedError()
    }

    override suspend fun updateTask(vararg task: Task) {
        throw NotImplementedError()
    }

    override suspend fun deleteTask(vararg task: Task) {
        throw NotImplementedError()
    }

    override suspend fun insertTask(vararg task: Task) {
        throw NotImplementedError()
    }

    override suspend fun deleteAllTasks() {
        throw NotImplementedError()
    }
}