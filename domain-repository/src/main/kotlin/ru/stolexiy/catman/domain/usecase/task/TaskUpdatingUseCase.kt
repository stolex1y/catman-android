package ru.stolexiy.catman.domain.usecase.task

import kotlinx.coroutines.CoroutineDispatcher
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.TransactionProvider
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.repository.task.TaskAddingRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingByPurposeRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class TaskUpdatingUseCase @Inject constructor(
    private val taskAdd: TaskAddingRepository,
    private val purposeGet: PurposeGettingRepository,
    private val taskGetByPurpose: TaskGettingByPurposeRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher,
    private val transactionProvider: TransactionProvider,
) {
    suspend operator fun invoke(vararg tasks: DomainTask): Result<Unit> {
        TODO()
    }
}