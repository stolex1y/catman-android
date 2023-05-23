package ru.stolexiy.catman.domain.repository.purpose

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose

interface PurposeRepository {
    fun getAll(): Flow<List<DomainPurpose>>
    fun get(id: Long): Flow<DomainPurpose?>
    fun getAllByCategoryOrderByPriority(categoryId: Long): Flow<List<DomainPurpose>>
    suspend fun update(vararg purposes: DomainPurpose): Unit
    suspend fun insert(vararg purposes: DomainPurpose): List<Long>
    suspend fun delete(vararg ids: Long): Unit
    suspend fun deleteAll(): Unit
}