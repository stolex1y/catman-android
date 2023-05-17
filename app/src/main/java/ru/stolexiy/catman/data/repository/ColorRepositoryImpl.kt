package ru.stolexiy.catman.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.data.datasource.local.dao.ColorDao
import ru.stolexiy.catman.data.datasource.local.model.ColorEntity
import ru.stolexiy.catman.data.datasource.local.model.toColorEntities
import ru.stolexiy.catman.domain.model.DomainColor
import ru.stolexiy.catman.domain.repository.ColorRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class ColorRepositoryImpl @Inject constructor(
    private val localDao: ColorDao,
    @Named(CoroutineModule.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : ColorRepository {
    override fun getColor(color: Int): Flow<DomainColor?> =
        localDao.get(color).distinctUntilChanged()
            .mapLatest { it?.toDomainColor() }
            .onEach { Timber.d("get color") }
            .flowOn(dispatcher)

    override fun getAllColors(): Flow<List<DomainColor>> =
        localDao.getAll().distinctUntilChanged()
            .mapLatest { it.map(ColorEntity::toDomainColor) }
            .onEach { Timber.d("get all colors: ${it.size}") }
            .flowOn(dispatcher)

    override suspend fun updateColor(vararg colors: DomainColor) =
        withContext(dispatcher) {
            Timber.d("update colors")
            localDao.update(*colors.toColorEntities())
        }

    override suspend fun deleteColor(vararg colors: DomainColor) =
        withContext(dispatcher) {
            Timber.d("delete colors")
            localDao.delete(*colors.toColorEntities())
        }

    override suspend fun deleteAllColors() {
        withContext(dispatcher) {
            Timber.d("delete all colors")
            localDao.deleteAll()
        }
    }

    override suspend fun createColor(vararg colors: DomainColor): Unit =
        withContext(dispatcher) {
            Timber.d("insert colors")
            localDao.insert(*colors.toColorEntities())
        }
}
