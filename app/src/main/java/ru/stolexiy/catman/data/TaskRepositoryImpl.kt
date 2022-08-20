package ru.stolexiy.catman.data

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Task
import ru.stolexiy.catman.domain.repository.TaskRepository

class TaskRepositoryImpl : TaskRepository {
    override fun getAllTasks(purposeId: Long): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getTask(id: Long): Flow<Task> {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task): Flow<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(task: Task) {
        TODO("Not yet implemented")
    }
}