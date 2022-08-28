package ru.stolexiy.catman.data

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.stolexiy.catman.data.datasource.local.dao.CategoryDao
import ru.stolexiy.catman.data.datasource.local.model.*
import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.repository.CategoryRepository
class CategoryRepositoryImpl(
    private val localDao: CategoryDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> =
        localDao.getAll().map { it.map(CategoryEntity::toCategory) }.flowOn(dispatcher)

    override suspend fun getAllCategoriesOnce(): List<Category> =
        withContext(dispatcher) {
            localDao.getAllOnce().map(CategoryEntity::toCategory)
        }

    override suspend fun getCategoryOnce(id: Long): Category =
        withContext(dispatcher) {
            localDao.getOnce(id).toCategory()
        }

    override fun getCategory(id: Long): Flow<Category> =
        localDao.get(id).map(CategoryEntity::toCategory).flowOn(dispatcher)

    override suspend fun updateCategory(vararg categories: Category) =
        withContext(dispatcher) {
            localDao.update(*categories.toCategoryEntities())
        }

    override suspend fun deleteCategory(vararg categories: Category) {
        withContext(dispatcher) {
            localDao.delete(*categories.toCategoryEntities())
        }
    }

    override suspend fun insertCategory(vararg categories: Category) =
        withContext(dispatcher) {
            localDao.insert(*categories.toCategoryEntities())
        }

    override suspend fun deleteAllCategories() {
        withContext(dispatcher) {
            localDao.deleteAll()
        }
    }

    override fun getAllCategoriesWithPurposes(): Flow<List<Category>> =
        localDao.getAllWithPurposes().map { it.toCategoriesWithPurposes() }.flowOn(dispatcher)

    override fun getCategoryWithPurposes(id: Long): Flow<Category> =
        localDao.getWithPurposes(id).map { it.toCategoriesWithPurposes() }.map { it.first() }.flowOn(dispatcher)
}

