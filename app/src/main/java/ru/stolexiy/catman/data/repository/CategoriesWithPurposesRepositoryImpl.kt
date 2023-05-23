package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.data.datasource.local.dao.CategoriesWithPurposesDao
import ru.stolexiy.catman.data.datasource.local.model.toDomainMap
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.category.CategoriesWithPurposesRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CategoriesWithPurposesRepositoryImpl @Inject constructor(
    private val localDao: CategoriesWithPurposesDao,
    @Named(CoroutineModule.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : CategoriesWithPurposesRepository {
    override fun getAll(): Flow<Map<DomainCategory, List<DomainPurpose>>> =
        localDao.getAllWithPurposes()
            .distinctUntilChanged()
            .map { it.toDomainMap() }
            .onEach { Timber.d("get all categories with purposes $it") }
            .flowOn(dispatcher)
}
