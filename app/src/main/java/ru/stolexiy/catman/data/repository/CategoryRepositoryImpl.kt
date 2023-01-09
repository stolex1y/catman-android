package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.CategoryDao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.toCategoryEntities
import ru.stolexiy.catman.data.datasource.local.model.toDomainMap
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoryRepository
import timber.log.Timber

class CategoryRepositoryImpl(
    private val localDao: CategoryDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<DomainCategory>> =
        localDao.getAll().distinctUntilChanged().map { it.map(CategoryEntity::toDomainCategory) }
            .onEach { Timber.d("get all categories: ${it.size}") }
            .flowOn(dispatcher)

    override suspend fun getAllCategoriesOnce(): List<DomainCategory> =
        withContext(dispatcher) {
            Timber.d("get all categories once")
            localDao.getAllOnce().map(CategoryEntity::toDomainCategory)
        }

    override suspend fun getCategoryOnce(id: Long): DomainCategory =
        withContext(dispatcher) {
            Timber.d("get category once")
            localDao.getOnce(id).toDomainCategory()
        }

    override fun getCategory(id: Long): Flow<DomainCategory> =
        localDao.get(id).distinctUntilChanged().map(CategoryEntity::toDomainCategory)
            .onEach { Timber.d("get category once") }
            .flowOn(dispatcher)

    override suspend fun updateCategory(vararg categories: DomainCategory) =
        withContext(dispatcher) {
            Timber.d("update categories with id: ${categories.map { it.id }}")
            localDao.update(*categories.toCategoryEntities())
        }

    override suspend fun deleteCategory(vararg categories: DomainCategory) {
        withContext(dispatcher) {
            Timber.d("delete categories with id: ${categories.map { it.id }}")
            localDao.delete(*categories.toCategoryEntities())
        }
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

    override fun getAllCategoriesWithPurposes(): Flow<Map<DomainCategory, List<DomainPurpose>>> =
        localDao.getAllWithPurposes().distinctUntilChanged().map { it.toDomainMap() }
            .onEach { Timber.d("get all categories with purposes") }
            .flowOn(dispatcher)
}

