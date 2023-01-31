package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainCategory

interface CategoryRepository {
    fun getAllCategories(): Flow<List<DomainCategory>>
    fun getCategory(id: Long): Flow<DomainCategory?>
    suspend fun isCategoryExist(id: Long): Boolean
    suspend fun updateCategory(vararg categories: DomainCategory)
    suspend fun insertCategory(vararg categories: DomainCategory): List<Long>
    suspend fun deleteCategory(vararg categories: DomainCategory)
    suspend fun deleteAllCategories()
}