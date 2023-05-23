package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.data.datasource.local.dao.PurposeDao
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.toPurposeEntities
import ru.stolexiy.catman.data.datasource.local.model.toPurposeEntity
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PurposeRepositoryImpl @Inject constructor(
    private val localDao: PurposeDao,
    @Named(CoroutineModule.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : PurposeRepository {
    override fun getAll(): Flow<List<DomainPurpose>> =
        localDao.getAll().distinctUntilChanged()
            .map { it.map(PurposeEntity::toDomainPurpose) }
            .onEach { Timber.d("get all purposes") }
            .flowOn(dispatcher)

    override fun get(id: Long): Flow<DomainPurpose?> =
        localDao.get(id).distinctUntilChanged()
            .map { it?.toDomainPurpose() }
            .onEach { Timber.d("get purpose: $id") }
            .flowOn(dispatcher)

    override fun getAllByCategoryOrderByPriority(categoryId: Long): Flow<List<DomainPurpose>> =
        localDao.getAllByCategoryOrderByPriority(categoryId)
            .distinctUntilChanged()
            .map { it.map(PurposeEntity::toDomainPurpose) }
            .onEach { Timber.d("get all purposes by category order by priority: $categoryId") }
            .flowOn(dispatcher)

    override suspend fun update(vararg purposes: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("update purposes: ${purposes.map { it.id }}")
            localDao.update(*purposes.toPurposeEntities())
        }

    override suspend fun delete(vararg ids: Long) =
        withContext(dispatcher) {
            Timber.d("delete purposes: $ids")
            ids.map { get(it).first() }
                .filterNotNull()
                .map { launch { localDao.delete(it.toPurposeEntity()) } }
                .joinAll()
        }

    override suspend fun insert(vararg purposes: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("insert purposes")
            localDao.insert(*purposes.toPurposeEntities())
        }

    override suspend fun deleteAll() {
        withContext(dispatcher) {
            Timber.d("delete all purposes")
            localDao.deleteAll()
        }
    }
}
