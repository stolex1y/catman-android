package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose

interface PurposeRepository {
    fun getPurpose(id: Long): Flow<DomainPurpose>
    fun getAllPurposes(): Flow<List<DomainPurpose>>
    suspend fun getAllPurposesOnce(): List<DomainPurpose>
    suspend fun getPurposeOnce(id: Long): DomainPurpose
    fun getAllPurposesByCategoryOrderByPriority(categoryId: Long): Flow<List<DomainPurpose>>

    suspend fun getAllPurposesByCategoryOrderByPriorityOnce(categoryId: Long): List<DomainPurpose>
    fun getPurposeWithTasks(id: Long): Flow<DomainPurpose>
    suspend fun updatePurpose(vararg domainPurposes: DomainPurpose)
    suspend fun insertPurpose(vararg domainPurposes: DomainPurpose)
    suspend fun deletePurpose(vararg domainPurposes: DomainPurpose)
    suspend fun deleteAllPurposes()
}