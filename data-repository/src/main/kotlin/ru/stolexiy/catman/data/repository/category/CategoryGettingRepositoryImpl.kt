package ru.stolexiy.catman.data.repository.category

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.category.CategoryCrudDao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import ru.stolexiy.common.Mappers.mapList
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class CategoryGettingRepositoryImpl @Inject constructor(
    private val localDao: CategoryCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : CategoryGettingRepository {
    override fun all(): Flow<Result<List<DomainCategory>>> =
        localDao.getAll().distinctUntilChanged()
            .mapList(CategoryEntity.Response::toDomainCategory)
            .onStart { Timber.d("get all categories") }
            .flowOn(dispatcher)
            .mapToResult()

    override suspend fun allOnce(): Result<List<DomainCategory>> = runCatching {
        withContext(dispatcher) {
            Timber.d("get all categories")
            localDao.getAllOnce().map(CategoryEntity.Response::toDomainCategory)
        }
    }

    override fun byId(id: Long): Flow<Result<DomainCategory?>> =
        localDao.get(id).distinctUntilChanged()
            .map { it?.toDomainCategory() }
            .onStart { Timber.d("get category: $id") }
            .flowOn(dispatcher)
            .mapToResult()

    override suspend fun byIdOnce(id: Long): Result<DomainCategory?> = runCatching {
        withContext(dispatcher) {
            Timber.d("get category: $id")
            localDao.getOnce(id)?.toDomainCategory()
        }
    }

    override suspend fun existsWithId(id: Long): Result<Boolean> = runCatching {
        withContext(dispatcher) {
            Timber.d("is category exist: $id")
            localDao.isCategoryExist(id)
        }
    }
}
