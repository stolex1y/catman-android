package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.data.datasource.local.dao.CategoryDao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.toCategoryEntities
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.CategoryRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val localDao: CategoryDao,
    @Named(CoroutineModule.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<DomainCategory>> =
        localDao.getAll().distinctUntilChanged()
            .mapLatest { it.map(CategoryEntity::toDomainCategory) }
            .onEach { Timber.d("get all categories: ${it.size}") }
            .flowOn(dispatcher)

    override fun getCategory(id: Long): Flow<DomainCategory?> =
        localDao.get(id).distinctUntilChanged()
            .mapLatest { it?.toDomainCategory() }
            .onEach { Timber.d("get category") }
            .flowOn(dispatcher)

    override suspend fun updateCategory(vararg categories: DomainCategory) =
        withContext(dispatcher) {
            Timber.d("update categories with id: ${categories.map { it.id }}")
            localDao.update(*categories.toCategoryEntities())
        }

    override suspend fun deleteCategory(vararg categories: DomainCategory) =
        withContext(dispatcher) {
            Timber.d("delete categories with id: ${categories.map { it.id }}")
            localDao.delete(*categories.toCategoryEntities())
        }

    override suspend fun insertCategory(vararg categories: DomainCategory) =
        withContext(dispatcher) {
            Timber.d("insert categories")
            localDao.insert(*categories.toCategoryEntities())
        }

    override suspend fun deleteAllCategories() {
        withContext(dispatcher) {
            Timber.d("delete all categories")
            localDao.deleteAll()
        }
    }

    override suspend fun isCategoryExist(id: Long) =
        withContext(dispatcher) {
            Timber.d("is category exist: $id")
            localDao.isCategoryExist(id)
        }
}
