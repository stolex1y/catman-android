package ru.stolexiy.catman.domain.usecase.category

import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.category.CategoryRepository
import javax.inject.Inject

class CategoryUpdatingUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(vararg categories: DomainCategory): Result<Unit> {
        if (categories.isEmpty())
            return Result.success(Unit)
        return runCatching {
            repository.update(*categories)
        }
    }
}
