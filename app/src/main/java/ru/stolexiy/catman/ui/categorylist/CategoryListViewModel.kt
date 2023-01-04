package ru.stolexiy.catman.ui.categorylist

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.ui.categorylist.model.CategoryListFragmentState
import ru.stolexiy.catman.ui.categorylist.model.toCategoryListItems
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import timber.log.Timber

class CategoryListViewModel(
    private val categoryRepository: CategoryRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    var state: StateFlow<CategoryListFragmentState>

    init {
            state = loadCategoriesWithPurposes()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = CategoryListFragmentState.IsLoading
                )
    }

    private fun loadCategoriesWithPurposes() = categoryRepository.getAllCategoriesWithPurposes()
//        .onEach { delay(5000) }
        .onEach { Timber.d("start loading categories with purposes") }
        .map { it.toCategoryListItems() }
        .map { CategoryListFragmentState.LoadedData(it) }
        .onEach { Timber.d("end mapping categories with purposes: ${it.data.size}") }
        .flowOn(dispatcher)
        .catch { Timber.e(it) }
}
