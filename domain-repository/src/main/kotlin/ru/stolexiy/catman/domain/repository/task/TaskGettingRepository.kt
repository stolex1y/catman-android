package ru.stolexiy.catman.domain.repository.task

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainTask

interface TaskGettingRepository {
    fun all(): Flow<Result<List<DomainTask>>>
    suspend fun allOnce(): Result<List<DomainTask>>

    fun byId(id: Long): Flow<Result<DomainTask?>>
    suspend fun byIdOnce(id: Long): Result<DomainTask?>
}
