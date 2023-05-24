package ru.stolexiy.catman.domain.usecase.category

import ru.stolexiy.catman.domain.repository.category.CategoryRepository
import javax.inject.Inject

class CategoryDeletingUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend fun byId(vararg ids: Long): Result<Unit> {
        if (ids.isEmpty())
            return Result.success(Unit)
        return runCatching {
            repository.delete(*ids)
        }
    }

    suspend fun all(): Result<Unit> {
        return runCatching {
            repository.deleteAll()
        }
    }
}
