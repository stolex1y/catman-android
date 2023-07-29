package ru.stolexiy.catman.data.repository.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeCrudDao
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables
import ru.stolexiy.catman.data.repository.Sort
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingByCategoryRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import ru.stolexiy.common.Mappers.mapList
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PurposeGettingByCategoryRepositoryImpl @Inject constructor(
    private val localDao: PurposeCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : PurposeGettingByCategoryRepository {
    override fun all(
        categoryId: Long,
    ): Flow<Result<List<DomainPurpose>>> {
        return localDao.getAllByCategory(categoryId).distinctUntilChanged()
            .mapList(PurposeEntity.Response::toDomainPurpose)
            .onStart { Timber.d("get all purposes by category: $categoryId") }
            .flowOn(dispatcher)
            .mapToResult()
    }

    override suspend fun allOnce(categoryId: Long): Result<List<DomainPurpose>> = runCatching {
        withContext(dispatcher) {
            Timber.d("get all purposes by category: $categoryId")
            localDao.getAllByCategoryOnce(categoryId)
                .map(PurposeEntity.Response::toDomainPurpose)
        }
    }

    override fun allOrderedByPriority(
        categoryId: Long,
        asc: Boolean
    ): Flow<Result<List<DomainPurpose>>> {
        return localDao.getAllByCategoryOrdered(
            categoryId,
            Sort.create(Tables.Purposes.Fields.PRIORITY, asc).query
        )
            .distinctUntilChanged()
            .mapList(PurposeEntity.Response::toDomainPurpose)
            .onStart { Timber.d("get all purposes by category ordered by priority: $categoryId") }
            .flowOn(dispatcher)
            .mapToResult()
    }

    override suspend fun allOrderedByPriorityOnce(
        categoryId: Long,
        asc: Boolean
    ): Result<List<DomainPurpose>> = runCatching {
        withContext(dispatcher) {
            Timber.d("get all purposes by category ordered by priority: $categoryId")
            localDao.getAllByCategoryOnceOrdered(
                categoryId,
                Sort.create(Tables.Purposes.Fields.PRIORITY, asc).query
            )
                .map(PurposeEntity.Response::toDomainPurpose)
        }
    }
}