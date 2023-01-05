package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import timber.log.Timber
import java.util.*

class PurposeCommon(
    private val dispatcher: CoroutineDispatcher,
    private val purposeRepository: PurposeRepository,
    private val categoryRepository: CategoryRepository
) {

    private val today: Calendar = Calendar.getInstance().apply {
        set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), 0, 0, 0)
    }

    suspend fun create(vararg domainPurposes: DomainPurpose) =
        withContext(dispatcher) {
            Timber.d("invoke create purposes with purposes: $domainPurposes")
            val addDomainPurposeEntities = mutableListOf<DomainPurpose>()
            domainPurposes.forEach { purpose ->
                require(isNotPastDeadline(purpose.deadline)) { "The deadline can't be past" }
                require(categoryRepository.isCategoryExist(purpose.categoryId)) { "Parent category must be exist" }
                purposeRepository.getAllPurposesByCategoryOrderByPriority(purpose.categoryId).collect { purposesByCategory ->
                    val nextPriority = purposesByCategory.lastOrNull()?.priority?.plus(1) ?: 1
                    addDomainPurposeEntities += purpose.copy(
                        isFinished = false,
                        progress = 0,
                        priority = nextPriority
                    )
                }
            }
            purposeRepository.insertPurpose(*addDomainPurposeEntities.toTypedArray())
            Timber.d("purposes added")
        }

    private fun isNotPastDeadline(calendar: Calendar) = calendar.timeInMillis >= today.timeInMillis
}