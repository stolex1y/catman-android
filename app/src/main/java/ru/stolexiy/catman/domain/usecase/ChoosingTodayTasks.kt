package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.PageRequest
import ru.stolexiy.catman.domain.model.PageResponse
import ru.stolexiy.catman.domain.repository.TaskRepository
import ru.stolexiy.catman.domain.util.toResult

class ChoosingTodayTasks(
    private val taskRepository: TaskRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    fun getTasksByPurpose(purposeId: Long, pageRequest: PageRequest<DomainTask>): Flow<Result<PageResponse<DomainTask>>> {
        return taskRepository.getAllTasksByPurpose(purposeId, pageRequest).toResult()
    }

    fun getTasksByAllPurposes(pageRequest: PageRequest<DomainTask>): Flow<Result<Map<DomainPurpose, PageResponse<DomainTask>>>> {
        return taskRepository.getAllTasksWithPurposes(pageRequest).toResult()
    }
}