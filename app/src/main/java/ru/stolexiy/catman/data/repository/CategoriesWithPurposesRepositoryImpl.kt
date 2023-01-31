package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import ru.stolexiy.catman.data.datasource.local.dao.CategoriesWithPurposesDao
import ru.stolexiy.catman.data.datasource.local.model.toDomainMap
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoriesWithPurposesRepository
import timber.log.Timber

class CategoriesWithPurposesRepositoryImpl(
    private val localDao: CategoriesWithPurposesDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoriesWithPurposesRepository {
    override fun getAllCategoriesWithPurposes(): Flow<Map<DomainCategory, List<DomainPurpose>>> =
        localDao.getAllWithPurposes()
            .distinctUntilChanged()
            .onEach { Timber.d("entities ${it}") }
            .mapLatest { it.toDomainMap() }
            .onEach { Timber.d("get all categories with purposes ${it}") }
            .flowOn(dispatcher)
}