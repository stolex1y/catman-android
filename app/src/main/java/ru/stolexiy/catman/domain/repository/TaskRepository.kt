package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Purpose
import ru.stolexiy.catman.domain.model.Task

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTask(id: Long): Flow<Task>
    fun getAllTasksByPurpose(purposeId: Long): Flow<List<Task>>
    fun getTaskWithSubtasks(id: Long): Flow<Task>
    suspend fun updateTask(vararg tasks: Task)
    suspend fun deleteTask(vararg tasks: Task)
    suspend fun insertTask(vararg tasks: Task)
    suspend fun deleteAllTasks()
}