package ru.stolexiy.catman.ui.dialog.purpose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import ru.stolexiy.catman.domain.usecase.AddPurposeToCategory
import ru.stolexiy.catman.domain.usecase.UseCases
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.purpose.model.toCategory
import kotlin.coroutines.coroutineContext

class PurposeSettingsViewModel(
    private val addPurposeUseCases: AddPurposeToCategory,
    private val categoryRepository: CategoryRepository,
    private val purposeRepository: PurposeRepository,
    private val externalScope: CoroutineScope
) : ViewModel() {
    var purpose: Purpose = Purpose()
    private lateinit var categories: List<Category>

    init {
        viewModelScope.launch {
            getCategories()
        }
    }

    fun addPurpose() {
        externalScope.launch {
            addPurposeUseCases(purpose.toDomainPurpose())
        }
        purpose = Purpose()
    }

    fun updatePurpose() {
        externalScope.launch {
            purposeRepository.updatePurpose(purpose.toDomainPurpose())
        }
    }

    suspend fun getCategories(): List<Category> {
        if (!this::categories.isInitialized)
            categories = categoryRepository.getAllCategoriesOnce().map(DomainCategory::toCategory)
        return categories
    }

}