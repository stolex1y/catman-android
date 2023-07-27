package ru.stolexiy.catman.domain.repository.purpose

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose

interface PurposeGettingByCategoryRepository {
    fun all(categoryId: Long): Flow<Result<List<DomainPurpose>>>
    suspend fun allOnce(categoryId: Long): Result<List<DomainPurpose>>

    fun allOrderedByPriority(
        categoryId: Long,
        asc: Boolean = true
    ): Flow<Result<List<DomainPurpose>>>

    suspend fun allOrderedByPriorityOnce(
        categoryId: Long,
        asc: Boolean = true
    ): Result<List<DomainPurpose>>
}
