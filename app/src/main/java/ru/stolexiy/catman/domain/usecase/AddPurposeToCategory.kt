package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.toPurposeEntity
import ru.stolexiy.catman.domain.model.Purpose
import ru.stolexiy.catman.domain.repository.PurposeRepository

class AddPurposeToCategory(
    private val dispatcher: CoroutineDispatcher,
    private val repository: PurposeRepository
) {
    suspend operator fun invoke(vararg purposes: Purpose) =
        withContext(dispatcher) {
            val addPurposeEntities = mutableListOf<Purpose>()
            purposes.forEach { purpose ->
                repository.getAllPurposesByCategoryOrderByPriorityOnce(purpose.categoryId).let { purposesByCategory ->
                    val nextPriority = purposesByCategory.lastOrNull()?.priority?.plus(1) ?: 1
                    addPurposeEntities += purpose.copy(priority = nextPriority)
                }
            }
            repository.insertPurpose(*addPurposeEntities.toTypedArray())
        }
}