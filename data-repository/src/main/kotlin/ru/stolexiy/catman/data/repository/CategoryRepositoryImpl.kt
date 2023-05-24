package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.CategoryDao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.toCategoryEntities
import ru.stolexiy.catman.data.datasource.local.model.toCategoryEntity
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.category.CategoryRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val localDao: CategoryDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : CategoryRepository {
    override fun getAll(): Flow<List<DomainCategory>> =
        localDao.getAll().distinctUntilChanged()
            .map { it.map(CategoryEntity::toDomainCategory) }
            .onEach { Timber.d("get all categories: ${it.size}") }
            .flowOn(dispatcher)

    override fun get(id: Long): Flow<DomainCategory?> =
        localDao.get(id).distinctUntilChanged()
            .map { it?.toDomainCategory() }
            .onEach { Timber.d("get category") }
            .flowOn(dispatcher)

    override suspend fun update(vararg categories: DomainCategory): Unit =
        withContext(dispatcher) {
            Timber.d("update categories with id: ${categories.map { it.id }}")
            localDao.update(*categories.toCategoryEntities())
        }

    override suspend fun delete(vararg ids: Long): Unit =
        withContext(dispatcher) {
            Timber.d("delete category with ids: $ids}")
            ids.map { get(it).first() }
                .filterNotNull()
                .map { launch { localDao.delete(it.toCategoryEntity()) } }
                .joinAll()
        }

    override suspend fun insert(vararg categories: DomainCategory): List<Long> =
        withContext(dispatcher) {
            Timber.d("insert categories")
            localDao.insert(*categories.toCategoryEntities())
        }

    override suspend fun deleteAll(): Unit =
        withContext(dispatcher) {
            Timber.d("delete all categories")
            localDao.deleteAll()
        }

    override suspend fun isExist(id: Long): Boolean =
        withContext(dispatcher) {
            Timber.d("is category exist: $id")
            localDao.isCategoryExist(id)
        }
}
