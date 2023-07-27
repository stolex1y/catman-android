package ru.stolexiy.catman.data.repository.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.task.TaskCrudDao
import ru.stolexiy.catman.data.datasource.local.model.Tables
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity
import ru.stolexiy.catman.data.repository.Sort
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.task.TaskGettingByPurposeRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import ru.stolexiy.common.Mappers.mapList
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TaskGettingByPurposeRepositoryImpl @Inject constructor(
    private val localDao: TaskCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
) : TaskGettingByPurposeRepository {
    override fun all(purposeId: Long): Flow<Result<List<DomainTask>>> {
        return localDao.getAllByPurpose(purposeId).distinctUntilChanged()
            .mapList(TaskEntity::toDomainTask)
            .onStart { Timber.d("get all tasks by purpose: $purposeId") }
            .flowOn(ioDispatcher)
            .mapToResult()
    }

    override suspend fun allOnce(purposeId: Long): Result<List<DomainTask>> = runCatching {
        withContext(ioDispatcher) {
            Timber.d("get all tasks by purpose: $purposeId")
            localDao.getAllByPurposeOnce(purposeId).map(TaskEntity::toDomainTask)
        }
    }

    override fun allOrderedByPriority(
        purposeId: Long,
        asc: Boolean
    ): Flow<Result<List<DomainTask>>> {
        return localDao.getAllByPurposeOrderedByPriority(
            purposeId,
            Sort.create(Tables.Tasks.Fields.PRIORITY, asc).query
        )
            .distinctUntilChanged()
            .mapList(TaskEntity::toDomainTask)
            .onStart { Timber.d("get all tasks by purpose ordered by priority: $purposeId") }
            .flowOn(ioDispatcher)
            .mapToResult()
    }

    override suspend fun allOrderedByPriorityOnce(
        purposeId: Long,
        asc: Boolean
    ): Result<List<DomainTask>> = runCatching {
        withContext(ioDispatcher) {
            Timber.d("get all tasks by purpose ordered by priority: $purposeId")
            localDao.getAllByPurposeOrderedByPriorityOnce(
                purposeId,
                Sort.create(Tables.Tasks.Fields.PRIORITY, asc).query
            )
                .map(TaskEntity::toDomainTask)
        }
    }
}
