package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.PurposeDao
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.toPurposeEntities
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.PurposeRepository
import timber.log.Timber

class PurposeRepositoryImpl(
    private val localDao: PurposeDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PurposeRepository {
    override fun getAllPurposes(): Flow<List<DomainPurpose>> =
        localDao.getAll().distinctUntilChanged()
            .mapLatest { it.map(PurposeEntity::toDomainPurpose) }
            .onEach { Timber.d("get all purposes") }
            .flowOn(dispatcher)

    override fun getPurpose(id: Long): Flow<DomainPurpose?> =
        localDao.get(id).distinctUntilChanged()
            .mapLatest { it?.toDomainPurpose() }
            .onEach { Timber.d("get purpose: $id") }
            .flowOn(dispatcher)

    override fun getAllPurposesByCategoryOrderByPriority(categoryId: Long): Flow<List<DomainPurpose>> =
        localDao.getAllByCategoryOrderByPriority(categoryId)
            .distinctUntilChanged()
            .mapLatest { it.map(PurposeEntity::toDomainPurpose) }
            .onEach { Timber.d("get all purposes by category order by priority: $categoryId") }
            .flowOn(dispatcher)

    override suspend fun updatePurpose(vararg domainPurposes: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("update purposes: ${domainPurposes.map { it.id }}")
            localDao.update(*domainPurposes.toPurposeEntities())
        }

    override suspend fun deletePurpose(vararg domainPurposes: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("delete purposes: ${domainPurposes.map { it.id }}")
            localDao.delete(*domainPurposes.toPurposeEntities())
        }

    override suspend fun insertPurpose(vararg domainPurposes: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("insert purposes")
            localDao.insert(*domainPurposes.toPurposeEntities())
        }

    override suspend fun deleteAllPurposes() {
        withContext(dispatcher) {
            Timber.d("delete all purposes")
            localDao.deleteAll()
        }
    }
}