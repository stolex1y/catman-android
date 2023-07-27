package ru.stolexiy.catman.data.repository.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeCrudDao
import ru.stolexiy.catman.data.datasource.local.model.toPurposeEntity
import ru.stolexiy.catman.domain.repository.purpose.PurposeDeletingRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class PurposeDeletingRepositoryImpl @Inject constructor(
    private val localDao: PurposeCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : PurposeDeletingRepository {
    override suspend fun byId(vararg ids: Long): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("delete purposes: ${ids.toList()}")
            ids.map { localDao.get(it).first() }
                .filterNotNull()
                .map { launch { localDao.delete(it.toDomainPurpose().toPurposeEntity()) } }
                .joinAll()
        }
    }

    override suspend fun all(): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("delete all purposes")
            localDao.deleteAll()
        }
    }
}