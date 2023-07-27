package ru.stolexiy.catman.domain.repository.category

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainCategory

interface CategoryGettingRepository {
    fun all(): Flow<Result<List<DomainCategory>>>
    suspend fun allOnce(): Result<List<DomainCategory>>
    fun byId(id: Long): Flow<Result<DomainCategory?>>
    suspend fun byIdOnce(id: Long): Result<DomainCategory?>
    suspend fun existsWithId(id: Long): Result<Boolean>
}
