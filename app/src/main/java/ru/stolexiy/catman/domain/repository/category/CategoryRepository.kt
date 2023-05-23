package ru.stolexiy.catman.domain.repository.category

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainCategory

interface CategoryRepository {
    fun getAll(): Flow<List<DomainCategory>>
    fun get(id: Long): Flow<DomainCategory?>
    suspend fun isExist(id: Long): Boolean
    suspend fun update(vararg categories: DomainCategory): Unit
    suspend fun insert(vararg categories: DomainCategory): List<Long>
    suspend fun delete(vararg ids: Long): Unit
    suspend fun deleteAll(): Unit
}
