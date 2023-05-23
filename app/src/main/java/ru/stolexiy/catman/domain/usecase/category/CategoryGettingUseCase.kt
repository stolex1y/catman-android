package ru.stolexiy.catman.domain.usecase.category

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.core.FlowExtensions.mapToResult
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.category.CategoryRepository
import javax.inject.Inject

class CategoryGettingUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    fun all(): Flow<Result<List<DomainCategory>>> =
        repository.getAll().mapToResult()

    fun byId(id: Long): Flow<Result<DomainCategory?>> =
        repository.get(id).mapToResult()

    suspend fun isExist(id: Long): Result<Boolean> =
        runCatching {
            repository.isExist(id)
        }
}
