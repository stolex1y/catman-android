package ru.stolexiy.catman.domain.usecase.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.Sort
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.repository.task.TaskAddingRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingByPurposeRepository
import ru.stolexiy.catman.domain.usecase.task.TaskConstraints.checkDeadlineIsNotPast
import ru.stolexiy.catman.domain.usecase.task.TaskConstraints.checkPurposeIsExist
import ru.stolexiy.catman.domain.usecase.task.TaskConstraints.checkStartTimeIsNotPast
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class TaskAddingUseCase @Inject constructor(
    private val taskAdd: TaskAddingRepository,
    private val purposeGet: PurposeGettingRepository,
    private val taskGetByPurpose: TaskGettingByPurposeRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(vararg tasks: DomainTask): Result<List<Long>> {
        if (tasks.isEmpty())
            return Result.success(emptyList())
        return runCatching {
            withContext(dispatcher) {
                tasks.map {
                    async {
                        it.checkPurposeIsExist(purposeGet)
                        it.checkDeadlineIsNotPast()
                        it.checkStartTimeIsNotPast()
                    }
                }.awaitAll()
                tasks.map { task ->
                    val lastTaskInPurpose = taskGetByPurpose.allOrderedByPriorityOnce(
                        task.purposeId,
                        Sort.Direction.DESC
                    ).getOrThrow().firstOrNull()
                    val nextPriority = (lastTaskInPurpose?.priority ?: 0) + 1
                    return@map task.copy(
                        priority = nextPriority
                    )
                }
                    .toTypedArray()
                    .run {
                        taskAdd(*this).getOrThrow()
                    }
            }
        }
    }
}
