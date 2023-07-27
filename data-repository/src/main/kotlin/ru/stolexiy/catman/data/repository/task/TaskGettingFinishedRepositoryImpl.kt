package ru.stolexiy.catman.data.repository.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.dao.task.TaskCrudDao
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.task.TaskGettingFinishedRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TaskGettingFinishedRepositoryImpl @Inject constructor(
    private val localDao: TaskCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
) : TaskGettingFinishedRepository {
    override fun all(): Flow<Result<List<DomainTask>>> {
        TODO()
    }

    override suspend fun allOnce(): Result<List<DomainTask>> {
        TODO()
    }
}
