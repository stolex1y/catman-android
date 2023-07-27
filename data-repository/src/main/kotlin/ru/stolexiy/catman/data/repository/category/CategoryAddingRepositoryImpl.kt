package ru.stolexiy.catman.data.repository.category

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.dao.category.CategoryCrudDao
import ru.stolexiy.catman.data.datasource.local.model.toCategoryEntity
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.category.CategoryAddingRepository
import ru.stolexiy.common.Mappers.mapToArray
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class CategoryAddingRepositoryImpl @Inject constructor(
    private val localDao: CategoryCrudDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : CategoryAddingRepository {
    override suspend operator fun invoke(vararg categories: DomainCategory): Result<List<Long>> =
        runCatching {
            withContext(dispatcher) {
                Timber.d("insert categories")
                localDao.insert(*categories.mapToArray(DomainCategory::toCategoryEntity))
            }
        }
}