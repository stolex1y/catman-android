package ru.stolexiy.catman.domain.usecase.category

import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.category.CategoryRepository
import javax.inject.Inject

class CategoryAddingUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(vararg categories: DomainCategory): Result<List<Long>> {
        if (categories.isEmpty())
            return Result.success(emptyList())
        return runCatching {
            repository.insert(*categories)
        }
    }
}
