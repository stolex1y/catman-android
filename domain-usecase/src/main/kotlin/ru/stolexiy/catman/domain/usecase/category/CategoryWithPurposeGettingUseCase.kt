package ru.stolexiy.catman.domain.usecase.category

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.category.CategoriesWithPurposesRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import javax.inject.Inject

class CategoryWithPurposeGettingUseCase @Inject constructor(
    private val repository: CategoriesWithPurposesRepository,
) {
    fun all(): Flow<Result<Map<DomainCategory, List<DomainPurpose>>>> =
        repository.getAll().mapToResult()
}
