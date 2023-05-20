package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.core.FlowExtensions.mapToResult
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.CategoryRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CategoryCrud @Inject constructor(
    private val categoryRepository: CategoryRepository,
    @Named(CoroutineModule.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    fun getAll() = categoryRepository.getAllCategories().mapToResult()

    fun get(id: Long) = categoryRepository.getCategory(id).mapToResult()

    suspend fun create(category: DomainCategory): Result<List<Long>> =
        kotlin.runCatching {
            Timber.d("create category '${category.name}'")
            categoryRepository.insertCategory(category)
        }

    suspend fun update(vararg categories: DomainCategory): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("update categories: ${categories.map { it.id }.joinToString(", ")}")
                categoryRepository.updateCategory(*categories)
            }
        }

    suspend fun delete(vararg categories: DomainCategory): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("delete categories: ${categories.map { it.id }.joinToString(", ")}")
                categoryRepository.deleteCategory(*categories)
            }
        }

    suspend fun clear(): Result<Unit> =
        kotlin.runCatching {
            Timber.d("clear categories")
            categoryRepository.deleteAllCategories()
        }
}
