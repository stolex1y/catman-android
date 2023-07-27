package ru.stolexiy.catman.data.repository.color

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
import ru.stolexiy.catman.data.datasource.local.dao.ColorDao
import ru.stolexiy.catman.data.datasource.local.model.ColorEntity
import ru.stolexiy.catman.data.datasource.local.model.toColorEntities
import ru.stolexiy.catman.data.datasource.local.model.toColorEntity
import ru.stolexiy.catman.domain.model.DomainColor
import ru.stolexiy.catman.domain.repository.color.ColorRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ColorRepositoryImpl @Inject constructor(
    private val localDao: ColorDao,
    @Named(CoroutineDispatcherNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) : ColorRepository {
    override fun get(color: Int): Flow<DomainColor?> =
        localDao.get(color).distinctUntilChanged()
            .map { it?.toDomainColor() }
            .onEach { Timber.d("get color") }
            .flowOn(dispatcher)

    override fun getAll(): Flow<List<DomainColor>> =
        localDao.getAll().distinctUntilChanged()
            .map { it.map(ColorEntity::toDomainColor) }
            .onEach { Timber.d("get all colors: ${it.size}") }
            .flowOn(dispatcher)

    override suspend fun update(vararg colors: DomainColor) =
        withContext(dispatcher) {
            Timber.d("update colors")
            localDao.update(*colors.toColorEntities())
        }

    override suspend fun delete(vararg colors: Int) =
        withContext(dispatcher) {
            Timber.d("delete colors")
            colors.map { get(it).first() }
                .filterNotNull()
                .map { launch { localDao.delete(it.toColorEntity()) } }
                .joinAll()
        }

    override suspend fun deleteAll() {
        withContext(dispatcher) {
            Timber.d("delete all colors")
            localDao.deleteAll()
        }
    }

    override suspend fun insert(vararg colors: DomainColor): Unit =
        withContext(dispatcher) {
            Timber.d("insert colors")
            localDao.insert(*colors.toColorEntities())
        }
}
