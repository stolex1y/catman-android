package ru.stolexiy.catman.data

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.stolexiy.catman.data.datasource.local.dao.CategoryDao
import ru.stolexiy.catman.data.datasource.local.model.*
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.CategoryRepository
class CategoryRepositoryImpl(
    private val localDao: CategoryDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<DomainCategory>> =
        localDao.getAll().map { it.map(CategoryEntity::toCategory) }.flowOn(dispatcher)

    override suspend fun getAllCategoriesOnce(): List<DomainCategory> =
        withContext(dispatcher) {
            localDao.getAllOnce().map(CategoryEntity::toCategory)
        }

    override suspend fun getCategoryOnce(id: Long): DomainCategory =
        withContext(dispatcher) {
            localDao.getOnce(id).toCategory()
        }

    override fun getCategory(id: Long): Flow<DomainCategory> =
        localDao.get(id).map(CategoryEntity::toCategory).flowOn(dispatcher)

    override suspend fun updateCategory(vararg categories: DomainCategory) =
        withContext(dispatcher) {
            localDao.update(*categories.toCategoryEntities())
        }

    override suspend fun deleteCategory(vararg categories: DomainCategory) {
        withContext(dispatcher) {
            localDao.delete(*categories.toCategoryEntities())
        }
    }

    override suspend fun insertCategory(vararg categories: DomainCategory) =
        withContext(dispatcher) {
            localDao.insert(*categories.toCategoryEntities())
        }

    override suspend fun deleteAllCategories() {
        withContext(dispatcher) {
            localDao.deleteAll()
        }
    }

    override fun getAllCategoriesWithPurposes(): Flow<List<DomainCategory>> =
        localDao.getAllWithPurposes().map { it.toCategoriesWithPurposes() }.flowOn(dispatcher)

    override fun getCategoryWithPurposes(id: Long): Flow<DomainCategory> =
        localDao.getWithPurposes(id).map { it.toCategoriesWithPurposes() }.map { it.first() }.flowOn(dispatcher)
}

