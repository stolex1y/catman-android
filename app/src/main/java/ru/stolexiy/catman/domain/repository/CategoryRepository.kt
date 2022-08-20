package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Category

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategory(id: Long): Flow<Category>
    suspend fun updateCategory(category: Category): Flow<Category>
    suspend fun insertCategory(category: Category): Flow<Category>
    suspend fun deleteCategory(category: Category)
//    fun getCategoryWithPurposes(id: Long): Flow<Category>
    fun getAllCategoriesWithPurposes(): Flow<List<Category>>
}