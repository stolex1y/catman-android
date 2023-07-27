package ru.stolexiy.catman.domain.usecase.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.Sort
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeAddingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingByCategoryRepository
import ru.stolexiy.catman.domain.usecase.purpose.PurposeConstraints.checkCategoryIsExist
import ru.stolexiy.catman.domain.usecase.purpose.PurposeConstraints.checkDeadlineIsNotPast
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class PurposeAddingUseCase @Inject constructor(
    private val purposeGetByCategory: PurposeGettingByCategoryRepository,
    private val categoryGet: CategoryGettingRepository,
    private val purposeAdd: PurposeAddingRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(vararg entities: DomainPurpose): Result<List<Long>> {
        if (entities.isEmpty())
            return Result.success(emptyList())
        return runCatching {
            withContext(dispatcher) {
                entities.forEach {
                    it.checkDeadlineIsNotPast()
                    it.checkCategoryIsExist(categoryGet)
                }
                entities.map { purpose ->
                    val lastPurposeInCategory =
                        purposeGetByCategory.allOrderedByPriorityOnce(
                            purpose.categoryId,
                            Sort.Direction.DESC
                        ).getOrThrow().firstOrNull()
                    val nextPriority = (lastPurposeInCategory?.priority ?: 0) + 1
                    return@map purpose.copy(
                        isFinished = false,
                        priority = nextPriority
                    )
                }
                    .toTypedArray()
                    .run {
                        purposeAdd(*this).getOrThrow()
                    }
            }
        }
    }
}
