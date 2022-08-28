package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Subtask
import ru.stolexiy.catman.domain.model.Task

interface SubtaskRepository {
    fun getAllSubtasks(): Flow<List<Subtask>>
    fun getSubtask(id: Long): Flow<Subtask>
    fun getAllSubtasksByTask(taskId: Long): Flow<List<Subtask>>
    suspend fun updateSubtask(vararg subtasks: Subtask)
    suspend fun deleteSubtask(vararg subtasks: Subtask)
    suspend fun insertSubtask(vararg subtasks: Subtask)
    suspend fun deleteAllSubtasks()
}