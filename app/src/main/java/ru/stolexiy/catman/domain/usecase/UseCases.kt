package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository

class UseCases(
    private val categoryRepository: CategoryRepository,
    private val purposeRepository: PurposeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
//    private val categoryWithPurposeRepository: CategoryWithPurposeRepository
) {
    val addPurposeToCategory: AddPurposeToCategory
        get() = AddPurposeToCategory(dispatcher, purposeRepository)
}
