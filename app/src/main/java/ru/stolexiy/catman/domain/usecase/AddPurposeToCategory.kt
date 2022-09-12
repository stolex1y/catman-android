package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.PurposeRepository
import timber.log.Timber

class AddPurposeToCategory(
    private val dispatcher: CoroutineDispatcher,
    private val repository: PurposeRepository
) {
    suspend operator fun invoke(vararg domainPurposes: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("invoke add purpose with p: ${domainPurposes[0]}")
            val addDomainPurposeEntities = mutableListOf<DomainPurpose>()
            domainPurposes.forEach { purpose ->
                repository.getAllPurposesByCategoryOrderByPriorityOnce(purpose.categoryId).let { purposesByCategory ->
                    val nextPriority = purposesByCategory.lastOrNull()?.priority?.plus(1) ?: 1
                    addDomainPurposeEntities += purpose.copy(priority = nextPriority)
                }
            }
            repository.insertPurpose(*addDomainPurposeEntities.toTypedArray())
        }
}