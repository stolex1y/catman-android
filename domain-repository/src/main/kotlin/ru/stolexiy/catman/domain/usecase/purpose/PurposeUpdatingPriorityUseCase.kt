package ru.stolexiy.catman.domain.usecase.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.repository.TransactionProvider
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeUpdatingRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class PurposeUpdatingPriorityUseCase @Inject constructor(
    private val purposeGet: PurposeGettingRepository,
    private val purposeUpdate: PurposeUpdatingRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher,
    private val transactionProvider: TransactionProvider,
) {
    suspend operator fun invoke(idToPriority: Map<Long, Int>): Result<Unit> {
        if (idToPriority.isEmpty())
            return Result.success(Unit)
        return runCatching {
            withContext(dispatcher) {
                transactionProvider.runInTransaction {
                    idToPriority.map { entry ->
                        val id = entry.key
                        val priority = entry.value
                        async {
                            val oldPurpose = purposeGet.byIdOnce(id).getOrNull()
                                ?: throw IllegalArgumentException()
                            oldPurpose.copy(
                                priority = priority
                            )
                        }
                    }.awaitAll().run { purposeUpdate(*this.toTypedArray()) }
                }
            }
        }
    }
}
