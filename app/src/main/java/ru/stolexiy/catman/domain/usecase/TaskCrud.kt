package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.PageRequest
import ru.stolexiy.catman.domain.model.PageResponse
import ru.stolexiy.catman.domain.model.Sort
import ru.stolexiy.catman.domain.repository.TaskRepository
import timber.log.Timber

class TaskCrud(
    private val dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) {
    fun get(id: Long) = taskRepository.getTask(id)

    fun getAllByPurpose(purposeId: Long, pageRequest: PageRequest<DomainTask>): Flow<PageResponse<DomainTask>> {
        Timber.d("get all tasks by purpose id $purposeId")
        return taskRepository.getAllTasksByPurpose(purposeId, pageRequest)
    }

    fun getAllByPurpose(purposeId: Long, sort: Sort<DomainTask>): Flow<PageResponse<DomainTask>> {
        return getAllByPurpose(purposeId, PageRequest(0, Int.MAX_VALUE, sort))
    }

    suspend fun create(task: DomainTask) =
        withContext(dispatcher) {
            Timber.d("create task '${task.name}'")
            taskRepository.insertTask(task)
        }

    suspend fun update(vararg tasks: DomainTask) =
        withContext(dispatcher) {
            Timber.d("update tasks: ${tasks.map { it.id }.joinToString(", ")}")
            taskRepository.updateTask(*tasks)
        }

    suspend fun delete(vararg tasks: DomainTask) =
        withContext(dispatcher) {
            Timber.d("delete tasks: ${tasks.map { it.id }.joinToString(", ")}")
            taskRepository.deleteTask(*tasks)
        }

    suspend fun clear() =
        withContext(dispatcher) {
            Timber.d("clear tasks")
            taskRepository.deleteAllTasks()
        }
}