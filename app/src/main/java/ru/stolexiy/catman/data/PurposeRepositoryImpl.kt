package ru.stolexiy.catman.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.PurposeDao
import ru.stolexiy.catman.data.datasource.local.model.*
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.PurposeRepository

class PurposeRepositoryImpl(
    private val localDao: PurposeDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PurposeRepository {
    override fun getAllPurposes(): Flow<List<DomainPurpose>> =
        localDao.getAll().map { it.map(PurposeEntity::toPurpose) }.flowOn(dispatcher)

    override fun getPurpose(id: Long): Flow<DomainPurpose> =
        localDao.get(id).map(PurposeEntity::toPurpose).flowOn(dispatcher)

    override suspend fun getAllPurposesOnce(): List<DomainPurpose> =
        withContext(dispatcher) {
            localDao.getAllOnce().map(PurposeEntity::toPurpose)
        }

    override suspend fun getPurposeOnce(id: Long): DomainPurpose =
        withContext(dispatcher) {
            localDao.getOnce(id).toPurpose()
        }

    override fun getAllPurposesByCategoryOrderByPriority(categoryId: Long): Flow<List<DomainPurpose>> =
        localDao.getAllByCategoryOrderByPriority(categoryId).map { it.map(PurposeEntity::toPurpose) }.flowOn(dispatcher)

    override suspend fun getAllPurposesByCategoryOrderByPriorityOnce(categoryId: Long): List<DomainPurpose> =
        withContext(dispatcher) {
            localDao.getAllByCategoryOrderByPriorityOnce(categoryId).map(PurposeEntity::toPurpose)
        }

    override suspend fun updatePurpose(vararg domainPurposes: DomainPurpose) =
        withContext(dispatcher) {
            localDao.update(*domainPurposes.toPurposeEntities())
        }

    override suspend fun deletePurpose(vararg domainPurposes: DomainPurpose) =
        withContext(dispatcher) {
            localDao.delete(*domainPurposes.toPurposeEntities())
        }

    override suspend fun insertPurpose(vararg domainPurposes: DomainPurpose) =
        withContext(dispatcher) {
            localDao.insert(*domainPurposes.toPurposeEntities())
        }

    override suspend fun deleteAllPurposes() {
        withContext(dispatcher) {
            localDao.deleteAll()
        }
    }

    override fun getPurposeWithTasks(id: Long): Flow<DomainPurpose> =
        localDao.getWithTasks(id).map { it.toPurposeWithTasks() }.map { it.first() }.flowOn(dispatcher)
}