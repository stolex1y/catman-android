package ru.stolexiy.catman.data.repository.category

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import ru.stolexiy.catman.data.datasource.local.dao.category.CategoryWithPurposesDao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.Tables
import ru.stolexiy.catman.data.repository.Sort
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.category.CategoryGettingWithPurposesRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import ru.stolexiy.common.Mappers.mapToMap
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class CategoryGettingWithPurposesRepositoryImpl @Inject constructor(
    private val localDao: CategoryWithPurposesDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : CategoryGettingWithPurposesRepository {
    override fun all(): Flow<Result<Map<DomainCategory, List<DomainPurpose>>>> {
        return localDao.getAll().distinctUntilChanged()
            .mapToMap(
                CategoryEntity.Response::toDomainCategory,
                PurposeEntity.Response::toDomainPurpose
            )
            .onStart { Timber.d("get all categories with purposes") }
            .flowOn(dispatcher)
            .mapToResult()
    }

    override fun allOrderedByPurposePriority(asc: Boolean): Flow<Result<Map<DomainCategory, List<DomainPurpose>>>> {
        return localDao.getAllOrdered(
            Sort.create(Tables.Categories.Fields.NAME, asc = true).then(
                Sort.create(Tables.Purposes.Fields.PRIORITY, asc)
            ).query
        ).distinctUntilChanged()
            .mapToMap(
                CategoryEntity.Response::toDomainCategory,
                PurposeEntity.Response::toDomainPurpose
            )
            .onStart { Timber.d("get all categories with purposes") }
            .flowOn(dispatcher)
            .mapToResult()
    }
}
