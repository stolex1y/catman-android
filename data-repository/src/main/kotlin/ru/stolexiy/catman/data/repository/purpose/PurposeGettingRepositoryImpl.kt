package ru.stolexiy.catman.data.repository.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeCrudDao
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import ru.stolexiy.common.Mappers.mapList
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class PurposeGettingRepositoryImpl @Inject constructor(
    private val localDao: PurposeCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : PurposeGettingRepository {
    override fun all(): Flow<Result<List<DomainPurpose>>> =
        localDao.getAll().distinctUntilChanged()
            .mapList(PurposeEntity.Response::toDomainPurpose)
            .onStart { Timber.d("get all purposes") }
            .flowOn(dispatcher)
            .mapToResult()

    override suspend fun allOnce(): Result<List<DomainPurpose>> {
        return runCatching {
            withContext(dispatcher) {
                Timber.d("get all purposes")
                localDao.getAllOnce().map(PurposeEntity.Response::toDomainPurpose)
            }
        }
    }

    override fun byId(id: Long): Flow<Result<DomainPurpose?>> =
        localDao.get(id).distinctUntilChanged()
            .map { it?.toDomainPurpose() }
            .onStart { Timber.d("get purpose: $id") }
            .flowOn(dispatcher)
            .mapToResult()

    override suspend fun byIdOnce(id: Long): Result<DomainPurpose?> {
        return runCatching {
            withContext(dispatcher) {
                Timber.d("get purpose: $id")
                localDao.getOnce(id)?.toDomainPurpose()
            }
        }
    }
}
