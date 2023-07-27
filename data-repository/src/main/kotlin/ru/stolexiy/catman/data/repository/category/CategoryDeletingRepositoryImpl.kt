package ru.stolexiy.catman.data.repository.category

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.category.CategoryCrudDao
import ru.stolexiy.catman.data.datasource.local.model.toCategoryEntity
import ru.stolexiy.catman.domain.repository.category.CategoryDeletingRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class CategoryDeletingRepositoryImpl @Inject constructor(
    private val localDao: CategoryCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : CategoryDeletingRepository {

    override suspend fun byId(vararg ids: Long): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("delete category with ids: $ids}")
            ids.map { localDao.getOnce(it) }
                .filterNotNull()
                .map { launch { localDao.delete(it.toDomainCategory().toCategoryEntity()) } }
                .joinAll()
        }
    }

    override suspend fun all(): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("delete all categories")
            localDao.deleteAll()
        }
    }
}
