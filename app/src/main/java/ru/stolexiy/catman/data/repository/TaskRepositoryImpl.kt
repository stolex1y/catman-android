package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.TaskRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
//    localDao: TaskDao,
    @Named(CoroutineModule.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : TaskRepository {
    override fun getAll(): Flow<List<DomainTask>> {
        throw NotImplementedError()
    }

    override fun get(id: Long): Flow<DomainTask?> {
        throw NotImplementedError()
    }

    override fun getAllByPurpose(purposeId: Long): Flow<List<DomainTask>> {
        throw NotImplementedError()
    }

    /*override fun getAllByPurpose(
        purposeId: Long,
        pageRequest: PageRequest<DomainTask>
    ): Flow<PageResponse<DomainTask>> {
        TODO("Not yet implemented")
    }*/

    /*override fun getAllTasksWithPurposes(pageRequest: PageRequest<DomainTask>): Flow<Map<DomainPurpose, PageResponse<DomainTask>>> {
        TODO("Not yet implemented")
    }*/

    override suspend fun update(vararg tasks: DomainTask) {
        TODO()
    }

    override suspend fun delete(vararg tasks: DomainTask) {
        TODO()
    }

    override suspend fun insert(vararg tasks: DomainTask): List<Long> {
        TODO()
    }

    override suspend fun deleteAll() {
        TODO()
    }
}
