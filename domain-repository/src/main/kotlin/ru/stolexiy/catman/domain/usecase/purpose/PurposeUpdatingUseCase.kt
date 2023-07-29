package ru.stolexiy.catman.domain.usecase.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.TransactionProvider
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeUpdatingRepository
import ru.stolexiy.catman.domain.usecase.purpose.PurposeConstraints.checkCategoryIsExist
import ru.stolexiy.catman.domain.usecase.purpose.PurposeConstraints.checkDeadlineIsNotPast
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class PurposeUpdatingUseCase @Inject constructor(
    private val purposeGet: PurposeGettingRepository,
    private val categoryGet: CategoryGettingRepository,
    private val purposeUpdate: PurposeUpdatingRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher,
    private val transactionProvider: TransactionProvider,
) {
    suspend operator fun invoke(vararg entities: DomainPurpose): Result<Unit> {
        if (entities.isEmpty())
            return Result.success(Unit)
        return runCatching {
            withContext(dispatcher) {
                transactionProvider.runInTransaction {
                    entities.map { updatedPurpose ->
                        async {
                            val oldPurpose = purposeGet.byIdOnce(updatedPurpose.id).getOrNull()
                                ?: throw IllegalArgumentException()
                            if (updatedPurpose.deadline != oldPurpose.deadline)
                                updatedPurpose.checkDeadlineIsNotPast()
                            updatedPurpose.checkCategoryIsExist(categoryGet)
                            updatedPurpose
//                        updatedPurpose.updatePurposeProgress(purposeWithTasksGet)
                        }
                    }.awaitAll().run { purposeUpdate(*this.toTypedArray()) }
                }
            }
        }
    }

    /*private suspend fun DomainPurpose.updatePurposeProgress(purposeWithTasksGet: PurposeGettingWithTasksRepository): DomainPurpose {
        val purposeWithTasks =
            purposeWithTasksGet.byIdOnce(id).getOrThrow() ?: throw IllegalArgumentException()
        val progress = if (purposeWithTasks.second.isEmpty())
            0.0
        else
            purposeWithTasks.second.map(DomainTask::progress).average()
        return this.copy(progress = progress)
    }*/
}
