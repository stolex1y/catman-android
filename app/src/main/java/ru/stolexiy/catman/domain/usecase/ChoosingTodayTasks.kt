package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.core.FlowExtensions.mapToResult
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.PageRequest
import ru.stolexiy.catman.domain.model.PageResponse
import ru.stolexiy.catman.domain.repository.TaskRepository
import javax.inject.Inject
import javax.inject.Named

class ChoosingTodayTasks @Inject constructor(
    private val taskRepository: TaskRepository,
    @Named(CoroutineModule.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    fun getTasksByPurpose(
        purposeId: Long,
        pageRequest: PageRequest<DomainTask>
    ): Flow<Result<PageResponse<DomainTask>>> {
        return taskRepository.getAllTasksByPurpose(purposeId, pageRequest).mapToResult()
    }

    fun getTasksByAllPurposes(pageRequest: PageRequest<DomainTask>): Flow<Result<Map<DomainPurpose, PageResponse<DomainTask>>>> {
        return taskRepository.getAllTasksWithPurposes(pageRequest).mapToResult()
    }
}
