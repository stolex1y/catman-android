package ru.stolexiy.catman.data.repository.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeWithTasksDao
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingWithTasksRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import ru.stolexiy.common.Mappers.mapToMap
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class PurposeGettingWithTasksRepositoryImpl @Inject constructor(
    private val localDao: PurposeWithTasksDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
) : PurposeGettingWithTasksRepository {

    override fun all(): Flow<Result<Map<DomainPurpose, List<DomainTask>>>> {
        return localDao.getAllWithTasks()
            .distinctUntilChanged()
            .mapToMap(PurposeEntity.Response::toDomainPurpose, TaskEntity::toDomainTask)
            .onStart { Timber.d("get all purposes with tasks") }
            .flowOn(ioDispatcher)
            .mapToResult()
    }

    override suspend fun allOnce(): Result<Map<DomainPurpose, List<DomainTask>>> =
        runCatching {
            withContext(ioDispatcher) {
                Timber.d("get all purposes with tasks")
                localDao.getAllWithTasksOnce()
                    .mapToMap(PurposeEntity.Response::toDomainPurpose, TaskEntity::toDomainTask)
            }
        }

    override fun byId(purposeId: Long): Flow<Result<Pair<DomainPurpose, List<DomainTask>>?>> =
        localDao.getByIdWithTasks(purposeId)
            .distinctUntilChanged()
            .mapToMap(PurposeEntity.Response::toDomainPurpose, TaskEntity::toDomainTask)
            .map {
                val entry = it.entries.firstOrNull() ?: return@map null
                return@map entry.key to entry.value
            }
            .onStart { Timber.d("get purpose with id ($purposeId) with tasks") }
            .flowOn(ioDispatcher)
            .mapToResult()

    override suspend fun byIdOnce(purposeId: Long): Result<Pair<DomainPurpose, List<DomainTask>>?> =
        runCatching {
            withContext(ioDispatcher) {
                Timber.d("get all purposes with tasks")
                val entry = localDao.getByIdWithTasksOnce(purposeId)
                    .mapToMap(PurposeEntity.Response::toDomainPurpose, TaskEntity::toDomainTask)
                    .entries
                    .firstOrNull() ?: return@withContext null
                return@withContext entry.key to entry.value
            }
        }
}
