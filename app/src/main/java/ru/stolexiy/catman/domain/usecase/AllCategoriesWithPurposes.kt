package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoriesWithPurposesRepository
import ru.stolexiy.catman.domain.util.toResult

class AllCategoriesWithPurposes(
    private val repository: CategoriesWithPurposesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    operator fun invoke(): Flow<Result<Map<DomainCategory, List<DomainPurpose>>>> {
        return repository.getAllCategoriesWithPurposes()
            .map { map ->
                map.mapValues { it.value.sortedBy(DomainPurpose::priority) }
            }.flowOn(dispatcher)
            .toResult()
    }
}