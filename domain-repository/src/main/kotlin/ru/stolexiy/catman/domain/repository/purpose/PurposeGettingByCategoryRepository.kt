package ru.stolexiy.catman.domain.repository.purpose

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.Sort

interface PurposeGettingByCategoryRepository {
    fun all(categoryId: Long): Flow<Result<List<DomainPurpose>>>
    suspend fun allOnce(categoryId: Long): Result<List<DomainPurpose>>

    fun allOrderedByPriority(
        categoryId: Long,
        direction: Sort.Direction
    ): Flow<Result<List<DomainPurpose>>>

    suspend fun allOrderedByPriorityOnce(
        categoryId: Long,
        direction: Sort.Direction
    ): Result<List<DomainPurpose>>
}
