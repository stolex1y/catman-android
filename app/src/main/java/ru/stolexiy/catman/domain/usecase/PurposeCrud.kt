package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.core.DateUtils.isNotPast
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import ru.stolexiy.catman.domain.util.cancellationTransparency
import ru.stolexiy.catman.domain.util.toResult
import timber.log.Timber

class PurposeCrud(
    private val dispatcher: CoroutineDispatcher,
    private val purposeRepository: PurposeRepository,
    private val categoryRepository: CategoryRepository
) {
    fun getAll(): Flow<Result<List<DomainPurpose>>> =
        purposeRepository.getAllPurposes().toResult()

    fun get(id: Long) = purposeRepository.getPurpose(id).toResult()

    suspend fun create(purpose: DomainPurpose): Result<Unit> =
        runCatching {
            withContext(dispatcher) {
                Timber.d("create purpose '${purpose.name}'")
                require(purpose.deadline.isNotPast()) { "The deadline can't be past" }
                require(categoryRepository.isCategoryExist(purpose.categoryId)) { "Parent category must be exist" }
                purposeRepository.getAllPurposesByCategoryOrderByPriorityOnce(purpose.categoryId)
                    .let { purposesByCategory ->
                        val nextPriority = purposesByCategory.lastOrNull()?.priority?.plus(1) ?: 1
                        purpose.copy(
                            isFinished = false,
                            progress = 0,
                            priority = nextPriority
                        ).run { purposeRepository.insertPurpose(this) }
                    }
                Timber.d("purpose added")
            }
        }.cancellationTransparency()

    suspend fun delete(vararg ids: Long): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("delete purposes: ${ids.joinToString(", ")}")
                ids.map {
                    launch {
                        purposeRepository.getPurposeOnce(it).let { purpose ->
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