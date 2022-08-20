package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Task

interface TaskRepository {
    fun getAllTasks(purposeId: Long): Flow<List<Task>>
    fun getTask(id: Long): Flow<Task>
    fun updateTask(task: Task): Flow<Task>
    suspend fun deleteTask(task: Task)
}