package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.PageRequest
import ru.stolexiy.catman.domain.model.PageResponse
import ru.stolexiy.catman.domain.model.Sort
import ru.stolexiy.catman.domain.repository.TaskRepository
import ru.stolexiy.catman.domain.util.cancellationTransparency
import ru.stolexiy.catman.domain.util.toResult
import timber.log.Timber

class TaskCrud(
    private val taskRepository: TaskRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    fun get(id: Long) = taskRepository.getTask(id)

    fun getAllByPurpose(purposeId: Long, pageRequest: PageRequest<DomainTask>): Flow<Result<PageResponse<DomainTask>>> {
        Timber.d("get all tasks by purpose id $purposeId")
        return taskRepository.getAllTasksByPurpose(purposeId, pageRequest).toResult()
    }

    fun getAllByPurpose(purposeId: Long, sort: Sort<DomainTask>): Flow<Result<PageResponse<DomainTask>>> {
        return getAllByPurpose(purposeId, PageRequest(0, Int.MAX_VALUE, sort))
    }

    suspend fun create(task: DomainTask): Result<List<Long>> =
        kotlin.runCatching {
            Timber.d("create task '${task.name}'")
            taskRepository.insertTask(task)
        }.cancellationTransparency()

    suspend fun update(vararg tasks: DomainTask): Result<Unit> =
        kotlin.runCatching {
            Timber.d("update tasks: ${tasks.map { it.id }.joinToString(", ")}")
            taskRepository.updateTask(*tasks)
        }.cancellationTransparency()

    suspend fun delete(vararg tasks: DomainTask): Result<Unit> =
        kotlin.runCatching {
            Timber.d("delete tasks: ${tasks.map { it.id }.joinToString(", ")}")
            taskRepository.deleteTask(*tasks)
        }.cancellationTransparency()

    suspend fun clear(): Result<Unit> =
        kotlin.runCatching {
            Timber.d("clear tasks")
            taskRepository.deleteAllTasks()
        }.cancellationTransparency()
}