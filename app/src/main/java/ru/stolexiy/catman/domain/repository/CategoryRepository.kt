package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Category

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategory(id: Long): Flow<Category>
    suspend fun getAllCategoriesOnce(): List<Category>
    suspend fun getCategoryOnce(id: Long): Category
    suspend fun updateCategory(vararg categories: Category)
    suspend fun insertCategory(vararg categories: Category)
    suspend fun deleteCategory(vararg categories: Category)
    suspend fun deleteAllCategories()

    fun getAllCategoriesWithPurposes(): Flow<List<Category>>
    fun getCategoryWithPurposes(id: Long): Flow<Category>
}