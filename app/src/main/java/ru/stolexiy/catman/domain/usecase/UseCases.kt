package ru.stolexiy.catman.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository

class UseCases(
    private val categoryRepository: CategoryRepository,
    private val purposeRepository: PurposeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    val purposeCommon: PurposeCommon
        get() = PurposeCommon(dispatcher, purposeRepository, categoryRepository)
}
