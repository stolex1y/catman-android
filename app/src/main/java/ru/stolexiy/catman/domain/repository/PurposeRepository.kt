package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose

interface PurposeRepository {
    fun getAllPurposes(): Flow<List<DomainPurpose>>
    fun getPurpose(id: Long): Flow<DomainPurpose?>
    fun getAllPurposesByCategoryOrderByPriority(categoryId: Long): Flow<List<DomainPurpose>>
    suspend fun updatePurpose(vararg domainPurposes: DomainPurpose)
    suspend fun insertPurpose(vararg domainPurposes: DomainPurpose): List<Long>
    suspend fun deletePurpose(vararg domainPurposes: DomainPurpose)
    suspend fun deleteAllPurposes()
}