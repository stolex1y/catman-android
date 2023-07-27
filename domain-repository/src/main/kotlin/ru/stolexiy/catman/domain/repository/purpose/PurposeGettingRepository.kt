package ru.stolexiy.catman.domain.repository.purpose

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose

interface PurposeGettingRepository {
    fun all(): Flow<Result<List<DomainPurpose>>>
    suspend fun allOnce(): Result<List<DomainPurpose>>
    fun byId(id: Long): Flow<Result<DomainPurpose?>>
    suspend fun byIdOnce(id: Long): Result<DomainPurpose?>
}
