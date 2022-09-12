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

//    private val ADDING_PURPOSE_HANDLE = "ADDING_PURPOSE"

    var state: StateFlow<CategoryListFragmentState>

    /*var addingPurpose: Purpose? = null
        set(value) {
            field = value
            handle[ADDING_PURPOSE_HANDLE] = value
        }*/

    init {
            state = loadCategoriesWithPurposes()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = CategoryListFragmentState.IsLoading
                )
//            addingPurpose = handle[ADDING_PURPOSE_HANDLE]
            /*viewModelScope.launch(coroutineExceptionHandler + dispatcher) {
                state.collect {
                    handle["saved"] = it
                    Timber.d(handle.get<CategoryListFragmentState>("saved").toString())
                }
            }*/
    }

    private fun loadCategoriesWithPurposes() = categoryRepository.getAllCategoriesWithPurposes()
//        .onEach { delay(5000) }
        .map { it.toCategoryListItems() }
        .map { CategoryListFragmentState.LoadedData(it) }
        .flowOn(dispatcher)
        .catch { Timber.e(it) }

}
