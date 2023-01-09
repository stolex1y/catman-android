package ru.stolexiy.catman.ui.dialog.purpose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.usecase.CategoryCrud
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.purpose.model.toCategory
import ru.stolexiy.catman.ui.util.validators.Conditions
import timber.log.Timber

class PurposeSettingsViewModel(
    private val application: CatmanApplication,
    private val purposeCrud: PurposeCrud,
    private val categoryCrud: CategoryCrud,
    private val externalScope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val requiredFieldCondition = Conditions.RequiredField<CharSequence>(application.applicationContext, R.string.required_field_error)

    val purpose: Purpose = savedStateHandle[PURPOSE] ?: Purpose()
//    private val purposeFlow: StateFlow<Purpose> = MutableStateFlow(purpose)
    /*flowOf(purpose)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            purpose
        ) as MutableStateFlow<Purpose>*/

    val categories: StateFlow<List<Category>> =
        getCategories().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        viewModelScope.launch {
//            purposeFlow.collectLatest {
//                savedStateHandle[PURPOSE] = it
//            }
        }
    }

    override fun onCleared() {
        saveState()
        super.onCleared()
    }

    fun addPurpose() {
        val addingPurpose = purpose.toDomainPurpose()
        //TODO WorkManager
        externalScope.launch {
            Timber.d("start adding purpose")
            delay(5000)
            purposeCrud.create(addingPurpose)
            Timber.d("end adding purpose")
        }
        clearState()
    }

    fun updatePurpose() {
        val updatingPurpose = purpose.toDomainPurpose()
        //TODO WorkManager
        externalScope.launch {
            Timber.d("start updating purpose")
            purposeCrud.update(updatingPurpose)
            Timber.d("end updating purpose")
        }
        clearState()
    }

    private fun getCategories() = categoryCrud.getAll().map { it.map(DomainCategory::toCategory) }

    private fun saveState() {
        externalScope.launch {
            savedStateHandle[PURPOSE] = purpose
            Timber.d("save purpose state")
        }
    }

    private fun clearState() {
        purpose.apply {
            categoryId = null
            name = ""
            deadline = null
            description = ""
        }
    }

    companion object {
        private const val PURPOSE: String = "PURPOSE"

        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CatmanApplication
                val savedStateHandle = extras.createSavedStateHandle()
                return PurposeSettingsViewModel(
                    application,
                    application.purposeCrud,
                    application.categoryCrud,
                    application.applicationScope,
                    savedStateHandle
                ) as T
            }
        }
    }
}