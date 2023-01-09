package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.PageRequest
import ru.stolexiy.catman.domain.model.PageResponse
import ru.stolexiy.catman.domain.repository.TaskRepository

class ChoosingTodayTasks(
    private val dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository,
) {
    fun getTasksByPurpose(purposeId: Long, pageRequest: PageRequest<DomainTask>): Flow<PageResponse<DomainTask>> {
        return taskRepository.getAllTasksByPurpose(purposeId, pageRequest)
    }

    fun getTasksByAllPurposes(pageRequest: PageRequest<DomainTask>): Flow<Map<DomainPurpose, PageResponse<DomainTask>>> {
        return taskRepository.getAllTasksWithPurposes(pageRequest)
    }
}