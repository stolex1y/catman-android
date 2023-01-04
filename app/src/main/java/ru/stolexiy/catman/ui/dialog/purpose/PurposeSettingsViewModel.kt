package ru.stolexiy.catman.ui.dialog.purpose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import ru.stolexiy.catman.domain.usecase.PurposeCommon
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.purpose.model.toCategory
import timber.log.Timber

class PurposeSettingsViewModel(
    private val addPurposeUseCases: PurposeCommon,
    private val categoryRepository: CategoryRepository,
    private val purposeRepository: PurposeRepository,
    private val externalScope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val ADDING_PURPOSE: String = "ADDING_PURPOSE"
    }

    val purpose: Purpose = savedStateHandle.get<Purpose>(ADDING_PURPOSE) ?: Purpose()

    private lateinit var categories: List<Category>
    private var getCategoriesJob: Job = viewModelScope.launch {
        categories = categoryRepository.getAllCategoriesOnce().map(DomainCategory::toCategory)
    }

    override fun onCleared() {
        saveState()
        super.onCleared()
    }

    private fun saveState() {
        externalScope.launch {
            savedStateHandle[ADDING_PURPOSE] = purpose
            Timber.d("save purpose state")
        }
    }

    fun addPurpose() {
        val addingPurpose = purpose.toDomainPurpose()
        externalScope.launch {
            Timber.d("start adding purpose")
            delay(5000)
            addPurposeUseCases(addingPurpose)
            Timber.d("end adding purpose")
        }
        clearState()
    }

    fun updatePurpose() {
        externalScope.launch {
            Timber.d("start updating purpose")
            purposeRepository.updatePurpose(purpose.toDomainPurpose())
            Timber.d("end updating purpose")
        }
    }

    suspend fun getCategories(): List<Category> {
        if (getCategoriesJob.isActive)
            getCategoriesJob.join()
        return categories
    }

    private fun clearState() {
        purpose.categoryId = null
        purpose.name = ""
        purpose.deadline = null
        purpose.description = ""
        saveState()
    }

}