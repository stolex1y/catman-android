package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.data.datasource.local.dao.CategoriesWithPurposesDao
import ru.stolexiy.catman.data.datasource.local.model.toDomainMap
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoriesWithPurposesRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class CategoriesWithPurposesRepositoryImpl @Inject constructor(
    private val localDao: CategoriesWithPurposesDao,
    @Named(CoroutineModule.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : CategoriesWithPurposesRepository {
    override fun getAllCategoriesWithPurposes(): Flow<Map<DomainCategory, List<DomainPurpose>>> =
        localDao.getAllWithPurposes()
            .distinctUntilChanged()
            .onEach { Timber.d("entities ${it}") }
            .mapLatest { it.toDomainMap() }
            .onEach { Timber.d("get all categories with purposes ${it}") }
            .flowOn(dispatcher)
}
