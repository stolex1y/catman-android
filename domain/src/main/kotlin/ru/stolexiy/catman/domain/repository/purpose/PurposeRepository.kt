package ru.stolexiy.catman.domain.repository.purpose

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose

interface PurposeRepository {
    fun getAll(): Flow<List<ru.stolexiy.catman.domain.model.DomainPurpose>>
    fun get(id: Long): Flow<ru.stolexiy.catman.domain.model.DomainPurpose?>
    fun getAllByCategoryOrderByPriority(categoryId: Long): Flow<List<ru.stolexiy.catman.domain.model.DomainPurpose>>
    suspend fun update(vararg purposes: ru.stolexiy.catman.domain.model.DomainPurpose): Unit
    suspend fun insert(vararg purposes: ru.stolexiy.catman.domain.model.DomainPurpose): List<Long>
    suspend fun delete(vararg ids: Long): Unit
    suspend fun deleteAll(): Unit
}