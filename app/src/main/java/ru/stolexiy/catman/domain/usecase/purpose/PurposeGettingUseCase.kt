package ru.stolexiy.catman.domain.usecase.purpose

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.core.FlowExtensions.mapToResult
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeRepository
import javax.inject.Inject

class PurposeGettingUseCase @Inject constructor(
    private val repository: PurposeRepository,
) {
    fun all(): Flow<Result<List<DomainPurpose>>> =
        repository.getAll().mapToResult()

    fun byId(id: Long): Flow<Result<DomainPurpose?>> =
        repository.get(id).mapToResult()

    fun allByCategoryOrderByPriority(categoryId: Long): Flow<Result<List<DomainPurpose>>> =
        repository.getAllByCategoryOrderByPriority(categoryId).mapToResult()
}
