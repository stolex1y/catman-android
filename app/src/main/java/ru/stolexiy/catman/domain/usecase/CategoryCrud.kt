package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.CategoryRepository
import timber.log.Timber

class CategoryCrud(
    private val dispatcher: CoroutineDispatcher,
    private val categoryRepository: CategoryRepository
) {
    fun getAll() = categoryRepository.getAllCategories()

    fun get(id: Long) = categoryRepository.getCategory(id)

    suspend fun create(category: DomainCategory) =
        withContext(dispatcher) {
            Timber.d("create category '${category.name}'")
            categoryRepository.insertCategory(category)
        }

    suspend fun update(vararg categories: DomainCategory) =
        withContext(dispatcher) {
            Timber.d("update categories: ${categories.map { it.id }.joinToString(", ")}")
            categoryRepository.updateCategory(*categories)
        }

    suspend fun delete(vararg categories: DomainCategory) =
        withContext(dispatcher) {
            Timber.d("delete categories: ${categories.map { it.id }.joinToString(", ")}")
            categoryRepository.deleteCategory(*categories)
        }

    suspend fun clear() =
        withContext(dispatcher) {
            Timber.d("clear categories")
            categoryRepository.deleteAllCategories()
        }
}