package ru.stolexiy.catman.domain.usecase.category

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.category.CategoriesWithPurposesRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class CategoryWithPurposeGettingUseCase @Inject constructor(
    private val repository: CategoriesWithPurposesRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    fun all(): Flow<Result<Map<DomainCategory, List<DomainPurpose>>>> =
        repository.getAll().mapToResult()

    fun allWithPurposeOrderingByPriority(): Flow<Result<Map<DomainCategory, List<DomainPurpose>>>> {
        return repository.getAll()
            .map { entries ->
                entries.mapValues { entry ->
                    entry.value.sortedBy(DomainPurpose::priority)
                }
            }.flowOn(dispatcher)
            .mapToResult()
    }
}
