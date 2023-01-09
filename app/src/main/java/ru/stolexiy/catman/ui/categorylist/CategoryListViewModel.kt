package ru.stolexiy.catman.ui.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.domain.usecase.AllCategoriesWithPurposes
import ru.stolexiy.catman.ui.categorylist.model.CategoryListFragmentState
import ru.stolexiy.catman.ui.categorylist.model.toCategoryListItems
import timber.log.Timber

class CategoryListViewModel(
    private val allCategoriesWithPurposes: AllCategoriesWithPurposes,
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

    private fun loadCategoriesWithPurposes() = allCategoriesWithPurposes()
//        .onStart { delay(5000) }
        .onStart { Timber.d("start loading categories with purposes") }
        .map { it.toCategoryListItems() }
        .map { CategoryListFragmentState.LoadedData(it) }
        .onEach { Timber.d("end mapping categories with purposes: ${it.data.size}") }
        .flowOn(dispatcher)
        .catch { Timber.e(it) }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as CatmanApplication
                return CategoryListViewModel(
                    application.allCategoriesWithPurposes
                ) as T
            }
        }
    }
}
