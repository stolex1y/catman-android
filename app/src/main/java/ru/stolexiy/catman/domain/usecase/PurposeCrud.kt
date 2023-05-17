package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import ru.stolexiy.catman.domain.util.DateUtils.isNotPast
import ru.stolexiy.catman.domain.util.cancellationTransparency
import ru.stolexiy.catman.domain.util.toResult
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class PurposeCrud @Inject constructor(
    private val purposeRepository: PurposeRepository,
    private val categoryRepository: CategoryRepository,
    @Named(CoroutineModule.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    fun getAll(): Flow<Result<List<DomainPurpose>>> =
        purposeRepository.getAllPurposes().toResult()

    fun get(id: Long) = purposeRepository.getPurpose(id).toResult()

    suspend fun create(purpose: DomainPurpose): Result<List<Long>> =
        runCatching {
            withContext(dispatcher) {
                Timber.d("create purpose '${purpose.name}'")
                require(purpose.deadline.isNotPast()) { "The deadline can't be past" }
                require(categoryRepository.isCategoryExist(purpose.categoryId)) { "Parent category must be exist" }
                purposeRepository.getAllPurposesByCategoryOrderByPriority(purpose.categoryId)
                    .first()
                    .let { purposesByCategory ->
                        val nextPriority = purposesByCategory.lastOrNull()?.priority?.plus(1) ?: 1
                        purpose.copy(
                            isFinished = false,
                            progress = 0,
                            priority = nextPriority
                        ).run { purposeRepository.insertPurpose(this) }
                    }
            }
        }.cancellationTransparency()

    suspend fun delete(vararg ids: Long): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("delete purposes: ${ids.joinToString(", ")}")
                ids.map {
                    launch {
                        purposeRepository.getPurpose(it).first()?.let { purpose ->
                            purposeRepository.deletePurpose(purpose)
                        }
                    }
                }.joinAll()
            }
        }.cancellationTransparency()

    suspend fun clear(): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("clear purposes")
                purposeRepository.deleteAllPurposes()
            }
        }.cancellationTransparency()

    suspend fun update(vararg purposes: DomainPurpose): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                if (purposes.isEmpty())
                    return@withContext
                Timber.d("update purposes '${purposes.map(DomainPurpose::id).joinToString(", ")}'")
                purposes.forEach { purpose ->
                    require(purpose.deadline.isNotPast()) { "The deadline can't be past" }
                    require(categoryRepository.isCategoryExist(purpose.categoryId)) { "Parent category must be exist" }
                }
                purposeRepository.updatePurpose(*purposes)
            }
        }.cancellationTransparency()
}
