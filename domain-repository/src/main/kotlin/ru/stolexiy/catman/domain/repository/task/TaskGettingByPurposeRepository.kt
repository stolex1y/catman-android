package ru.stolexiy.catman.domain.repository.task

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainTask

interface TaskGettingByPurposeRepository {
    fun all(
        purposeId: Long,
    ): Flow<Result<List<DomainTask>>>

    suspend fun allOnce(
        purposeId: Long,
    ): Result<List<DomainTask>>

    fun allOrderedByPriority(
        purposeId: Long,
        asc: Boolean = false
    ): Flow<Result<List<DomainTask>>>

    suspend fun allOrderedByPriorityOnce(
        purposeId: Long,
        asc: Boolean = false
    ): Result<List<DomainTask>>
}
