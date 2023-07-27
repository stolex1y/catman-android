package ru.stolexiy.catman.data.repository.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.task.TaskCrudDao
import ru.stolexiy.catman.data.datasource.local.model.toTaskEntity
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.task.TaskAddingRepository
import ru.stolexiy.common.Mappers.mapToArray
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TaskAddingRepositoryImpl @Inject constructor(
    private val localDao: TaskCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
) : TaskAddingRepository {
    override suspend operator fun invoke(vararg tasks: DomainTask): Result<List<Long>> =
        runCatching {
            withContext(ioDispatcher) {
                Timber.d("create tasks")
                localDao.insert(*tasks.mapToArray(DomainTask::toTaskEntity))
            }
        }
}
