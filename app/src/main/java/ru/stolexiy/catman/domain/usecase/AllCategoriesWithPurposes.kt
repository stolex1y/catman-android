package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoryRepository

class AllCategoriesWithPurposes(
    private val dispatcher: CoroutineDispatcher,
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(): Flow<Map<DomainCategory, List<DomainPurpose>>> {
        return categoryRepository.getAllCategoriesWithPurposes()
            .mapLatest { map ->
                map.mapValues { it.value.sortedBy(DomainPurpose::priority) }
            }.flowOn(dispatcher)
    }
}