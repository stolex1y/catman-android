package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import ru.stolexiy.catman.domain.util.DateValidation.isNotPastDeadline
import timber.log.Timber

class PurposeCrud(
    private val dispatcher: CoroutineDispatcher,
    private val purposeRepository: PurposeRepository,
    private val categoryRepository: CategoryRepository
) {
    fun getAll() = purposeRepository.getAllPurposes()

    fun get(id: Long) = purposeRepository.getPurpose(id)

    suspend fun create(purpose: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("create purpose '${purpose.name}'")
            require(isNotPastDeadline(purpose.deadline)) { "The deadline can't be past" }
            require(categoryRepository.isCategoryExist(purpose.categoryId)) { "Parent category must be exist" }
            purposeRepository.getAllPurposesByCategoryOrderByPriorityOnce(purpose.categoryId).let { purposesByCategory ->
                val nextPriority = purposesByCategory.lastOrNull()?.priority?.plus(1) ?: 1
                purpose.copy(
                    isFinished = false,
                    progress = 0,
                    priority = nextPriority
                ).run { purposeRepository.insertPurpose(this) }
            }
            Timber.d("purpose added")
        }

    suspend fun delete(vararg ids: Long) =
        withContext(dispatcher) {
            Timber.d("delete purposes: ${ids.joinToString(", ")}")
            ids.map {
                launch {
                    purposeRepository.getPurposeOnce(it).let { purpose ->
                        purposeRepository.deletePurpose(purpose)
                    }
                }
            }
        }

    suspend fun clear() =
        withContext(dispatcher) {
            Timber.d("clear purposes")
            purposeRepository.deleteAllPurposes()
        }

    suspend fun update(vararg purposes: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("update purposes '${purposes.map(DomainPurpose::id).joinToString(", ")}'")
            purposes.forEach { purpose ->
                require(isNotPastDeadline(purpose.deadline)) { "The deadline can't be past" }
                require(categoryRepository.isCategoryExist(purpose.categoryId)) { "Parent category must be exist" }
            }
            purposeRepository.updatePurpose(*purposes)
        }
}