package ru.stolexiy.catman.data.repository.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.task.TaskCrudDao
import ru.stolexiy.catman.domain.repository.task.TaskDeletingRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TaskDeletingRepositoryImpl @Inject constructor(
    private val localDao: TaskCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
) : TaskDeletingRepository {
    override suspend fun byId(vararg ids: Long): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            Timber.d("delete tasks: ${ids.toList()}")
            ids.map { localDao.getOnce(it) }
                .filterNotNull()
                .map { launch { localDao.delete(it) } }
                .joinAll()
        }
    }

    override suspend fun all(): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            Timber.d("delete all tasks")
            localDao.deleteAll()
        }
    }
}
