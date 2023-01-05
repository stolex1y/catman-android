package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose

interface CategoryRepository {
    fun getAllCategories(): Flow<List<DomainCategory>>
    fun getCategory(id: Long): Flow<DomainCategory>
    suspend fun isCategoryExist(id: Long): Boolean
    suspend fun getAllCategoriesOnce(): List<DomainCategory>
    suspend fun getCategoryOnce(id: Long): DomainCategory
    suspend fun updateCategory(vararg categories: DomainCategory)
    suspend fun insertCategory(vararg categories: DomainCategory)
    suspend fun deleteCategory(vararg categories: DomainCategory)
    suspend fun deleteAllCategories()
    fun getAllCategoriesWithPurposes(): Flow<Map<DomainCategory, List<DomainPurpose>>>
}