package ru.stolexiy.catman.ui.categorylist

import android.service.controls.Control.StatefulBuilder
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.ui.categorylist.model.CategoryListFragmentState
import ru.stolexiy.catman.ui.categorylist.model.toCategoryListItems
import timber.log.Timber

class CategoryListViewModel(
    private val handle: SavedStateHandle,
    private val dispatcher: CoroutineDispatcher,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

//    private val _state = MutableStateFlow<CategoryListFragmentState>(CategoryListFragmentState.Init)
//    val state: StateFlow<CategoryListFragmentState>
//        get() = _state
    val state: StateFlow<CategoryListFragmentState> = loadCategoriesWithPurposes()

    init {
        /*viewModelScope.launch {
            categoryRepository.getAllCategoriesWithPurposes()
                .map { it.toCategoryListItems() }
                .flowOn(dispatcher)
                .onStart {
                    _state.value = CategoryListFragmentState.IsLoading
                }
                .catch { Timber.e(it) }
                .collect { list ->
                    _state.value = CategoryListFragmentState.LoadedData(list)
                }
        }*/
    }

    private fun loadCategoriesWithPurposes() = categoryRepository.getAllCategoriesWithPurposes()
        .map { it.toCategoryListItems() }
        .map { CategoryListFragmentState.LoadedData(it) }
        .flowOn(dispatcher)
        .catch { Timber.e(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryListFragmentState.IsLoading
        )

    class Factory(
        private val savedStateRegistryOwner: SavedStateRegistryOwner,
        private val dispatcher: CoroutineDispatcher,
        private val categoryRepository: CategoryRepository
    ) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return modelClass.getConstructor(SavedStateHandle::class.java, CoroutineDispatcher::class.java, CategoryRepository::class.java)
                .newInstance(handle, dispatcher, categoryRepository)
        }
    }

}
