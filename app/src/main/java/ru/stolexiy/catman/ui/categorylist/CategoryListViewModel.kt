package ru.stolexiy.catman.ui.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.usecase.AllCategoriesWithPurposes
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.categorylist.model.toCategoryListItems
import ru.stolexiy.catman.ui.util.state.ViewState
import timber.log.Timber

class CategoryListViewModel(
    private val mApplication: CatmanApplication,
    private val mAllCategoriesWithPurposes: AllCategoriesWithPurposes,
    private val mDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val mState: MutableStateFlow<State> = MutableStateFlow(State.Init)
    var state: StateFlow<State> = mState.asStateFlow()

    init {
        initState()
    }

    private fun initState() {
        viewModelScope.launch(mApplication.coroutineExceptionHandler + Dispatchers.Main) {
            coroutineScope {
                loadCategoriesWithPurposes().collectLatest {
                    mState.value = State.Loaded(it)
                }
            }
        }
    }

    private fun loadCategoriesWithPurposes() = mAllCategoriesWithPurposes()
        .onStart { Timber.d("start loading categories with purposes") }
        .onEach(this::handleError)
        .mapNotNull {
            Timber.d("map to category list items ${it.getOrNull()?.size}")
            it.getOrNull()?.toCategoryListItems()
        }
        .flowOn(mDispatcher)

    private fun <T> handleError(result: Result<T>) {
        if (result.isFailure) {
            mState.value = State.Error(mApplication.getString(R.string.internal_error))
            throw CancellationException()
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as CatmanApplication
                return CategoryListViewModel(
                    application,
                    application.allCategoriesWithPurposes
                ) as T
            }
        }
    }

    sealed class State : ViewState {
        object Init : State()
        data class Error(val error: String) : State()
        data class Loaded(val data: List<CategoryListItem>) : State()
        object Loading :
    }

}
