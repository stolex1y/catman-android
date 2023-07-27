package ru.stolexiy.catman.domain.repository.task

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainTask

interface TaskGettingFinishedRepository {
    fun all(): Flow<Result<List<DomainTask>>>
    suspend fun allOnce(): Result<List<DomainTask>>
}
