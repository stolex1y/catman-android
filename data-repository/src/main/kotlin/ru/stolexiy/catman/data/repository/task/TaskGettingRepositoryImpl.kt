package ru.stolexiy.catman.data.repository.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.task.TaskCrudDao
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.task.TaskGettingRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import ru.stolexiy.common.Mappers.mapList
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TaskGettingRepositoryImpl @Inject constructor(
    private val localDao: TaskCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
) : TaskGettingRepository {

    override fun all(): Flow<Result<List<DomainTask>>> {
        return localDao.getAll().distinctUntilChanged()
            .mapList(TaskEntity::toDomainTask)
            .onStart { Timber.d("get all tasks") }
            .flowOn(ioDispatcher)
            .mapToResult()
    }

    override suspend fun allOnce(): Result<List<DomainTask>> {
        return runCatching {
            withContext(ioDispatcher) {
                Timber.d("get all tasks")
                localDao.getAllOnce()
                    .map(TaskEntity::toDomainTask)
            }
        }
    }

    override fun byId(id: Long): Flow<Result<DomainTask?>> =
        localDao.get(id).distinctUntilChanged()
            .map { it?.toDomainTask() }
            .onStart { Timber.d("get task by id $id") }
            .flowOn(ioDispatcher)
            .mapToResult()

    override suspend fun byIdOnce(id: Long): Result<DomainTask?> = runCatching {
        withContext(ioDispatcher) {
            Timber.d("get task by id $id")
            localDao.getOnce(id)?.toDomainTask()
        }
    }
}
