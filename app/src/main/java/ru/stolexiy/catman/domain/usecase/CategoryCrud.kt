package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.util.cancellationTransparency
import ru.stolexiy.catman.domain.util.toResult
import timber.log.Timber

class CategoryCrud(
    private val categoryRepository: CategoryRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    fun getAll() = categoryRepository.getAllCategories().toResult()

    fun get(id: Long) = categoryRepository.getCategory(id).toResult()

    suspend fun create(category: DomainCategory): Result<List<Long>> =
        kotlin.runCatching {
            Timber.d("create category '${category.name}'")
            categoryRepository.insertCategory(category)
        }.cancellationTransparency()

    suspend fun update(vararg categories: DomainCategory): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("update categories: ${categories.map { it.id }.joinToString(", ")}")
                categoryRepository.updateCategory(*categories)
            }
        }.cancellationTransparency()

    suspend fun delete(vararg categories: DomainCategory): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("delete categories: ${categories.map { it.id }.joinToString(", ")}")
                categoryRepository.deleteCategory(*categories)
            }
        }.cancellationTransparency()

    suspend fun clear(): Result<Unit> =
        kotlin.runCatching {
            Timber.d("clear categories")
            categoryRepository.deleteAllCategories()
        }.cancellationTransparency()
}