package ru.stolexiy.catman.data.repository.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeCrudDao
import ru.stolexiy.catman.data.datasource.local.model.toPurposeEntity
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeUpdatingRepository
import ru.stolexiy.common.Mappers.mapToArray
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class PurposeUpdatingRepositoryImpl @Inject constructor(
    private val localDao: PurposeCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : PurposeUpdatingRepository {

    override suspend operator fun invoke(vararg purposes: DomainPurpose): Result<Unit> =
        runCatching {
            withContext(dispatcher) {
                Timber.d("update purposes with IDs: ${purposes.joinToString { it.id.toString() }}")
                localDao.update(*purposes.mapToArray(DomainPurpose::toPurposeEntity))
            }
        }
}
