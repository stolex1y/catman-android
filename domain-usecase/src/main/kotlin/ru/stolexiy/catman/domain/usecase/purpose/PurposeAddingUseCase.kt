package ru.stolexiy.catman.domain.usecase.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class PurposeAddingUseCase @Inject constructor(
    private val purposeRepository: PurposeRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(vararg purposes: DomainPurpose): Result<List<Long>> {
        if (purposes.isEmpty())
            return Result.success(emptyList())
        return runCatching {
            withContext(dispatcher) {
                purposes.map { purpose ->
                    val lastPurposeInCategory =
                        purposeRepository.getAllByCategoryOrderByPriority(purpose.categoryId)
                            .first()
                    val nextPriority = lastPurposeInCategory.lastOrNull()?.priority ?: 0
                    return@map purpose.copy(
                        isFinished = false,
                        progress = 0,
                        priority = nextPriority
                    )
                }
                    .toTypedArray()
                    .run {
                        purposeRepository.insert(*this)
                    }
            }
        }
    }
}
