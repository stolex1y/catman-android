package ru.stolexiy.catman.domain.repository.task

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.model.Sort

interface TaskGettingByPurposeRepository {
    fun all(
        purposeId: Long,
    ): Flow<Result<List<DomainTask>>>

    suspend fun allOnce(
        purposeId: Long,
    ): Result<List<DomainTask>>

    fun allOrderedByPriority(
        purposeId: Long,
        direction: Sort.Direction
    ): Flow<Result<List<DomainTask>>>

    suspend fun allOrderedByPriorityOnce(
        purposeId: Long,
        direction: Sort.Direction
    ): Result<List<DomainTask>>
}
