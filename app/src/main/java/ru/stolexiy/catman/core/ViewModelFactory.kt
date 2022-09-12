package ru.stolexiy.catman.core

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import ru.stolexiy.catman.domain.usecase.UseCases
import ru.stolexiy.catman.ui.categorylist.CategoryListViewModel
import ru.stolexiy.catman.ui.dialog.purpose.PurposeSettingsViewModel

class ViewModelFactory(
    private val savedStateRegistryOwner: SavedStateRegistryOwner,
    private val categoryRepository: CategoryRepository,
    private val purposeRepository: PurposeRepository,
    private val useCases: UseCases,
    private val externalScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {

    constructor(
        savedStateRegistryOwner: SavedStateRegistryOwner,
        application: CatmanApplication
    ) : this(
        savedStateRegistryOwner,
        application.categoryRepository,
        application.purposeRepository,
        application.useCases,
        application.applicationScope
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when (modelClass) {
            CategoryListViewModel::class.java -> CategoryListViewModel(categoryRepository, dispatcher) as T
            PurposeSettingsViewModel::class.java -> PurposeSettingsViewModel(useCases.addPurposeToCategory, categoryRepository, purposeRepository, externalScope) as T
            else -> throw IllegalArgumentException("Couldn't create view model: unknown class")
        }
    }
}