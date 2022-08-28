package ru.stolexiy.catman.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.PurposeDao
import ru.stolexiy.catman.data.datasource.local.model.*
import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.model.Purpose
import ru.stolexiy.catman.domain.repository.PurposeRepository

class PurposeRepositoryImpl(
    private val localDao: PurposeDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PurposeRepository {
    override fun getAllPurposes(): Flow<List<Purpose>> =
        localDao.getAll().map { it.map(PurposeEntity::toPurpose) }.flowOn(dispatcher)

    override fun getPurpose(id: Long): Flow<Purpose> =
        localDao.get(id).map(PurposeEntity::toPurpose).flowOn(dispatcher)

    override suspend fun getAllPurposesOnce(): List<Purpose> =
        withContext(dispatcher) {
            localDao.getAllOnce().map(PurposeEntity::toPurpose)
        }

    override suspend fun getPurposeOnce(id: Long): Purpose =
        withContext(dispatcher) {
            localDao.getOnce(id).toPurpose()
        }

    override fun getAllPurposesByCategoryOrderByPriority(categoryId: Long): Flow<List<Purpose>> =
        localDao.getAllByCategoryOrderByPriority(categoryId).map { it.map(PurposeEntity::toPurpose) }.flowOn(dispatcher)

    override suspend fun getAllPurposesByCategoryOrderByPriorityOnce(categoryId: Long): List<Purpose> =
        withContext(dispatcher) {
            localDao.getAllByCategoryOrderByPriorityOnce(categoryId).map(PurposeEntity::toPurpose)
        }

    override suspend fun updatePurpose(vararg purposes: Purpose) =
        withContext(dispatcher) {
            localDao.update(*purposes.toPurposeEntities())
        }

    override suspend fun deletePurpose(vararg purposes: Purpose) =
        withContext(dispatcher) {
            localDao.delete(*purposes.toPurposeEntities())
        }

    override suspend fun insertPurpose(vararg purposes: Purpose) =
        withContext(dispatcher) {
            localDao.insert(*purposes.toPurposeEntities())
        }

    override suspend fun deleteAllPurposes() {
        withContext(dispatcher) {
            localDao.deleteAll()
        }
    }

    override fun getPurposeWithTasks(id: Long): Flow<Purpose> =
        localDao.getWithTasks(id).map { it.toPurposeWithTasks() }.map { it.first() }.flowOn(dispatcher)
}