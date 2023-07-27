package ru.stolexiy.catman.data.repository.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeCrudDao
import ru.stolexiy.catman.data.datasource.local.model.toPurposeEntity
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeAddingRepository
import ru.stolexiy.common.Mappers.mapToArray
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class PurposeAddingRepositoryImpl @Inject constructor(
    private val localDao: PurposeCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : PurposeAddingRepository {
    override suspend operator fun invoke(vararg purposes: DomainPurpose): Result<List<Long>> =
        runCatching {
            withContext(dispatcher) {
                Timber.d("insert purposes")
                localDao.insert(*purposes.mapToArray(DomainPurpose::toPurposeEntity))
            }
        }
}
