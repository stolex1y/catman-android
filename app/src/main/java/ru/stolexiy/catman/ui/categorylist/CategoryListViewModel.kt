package ru.stolexiy.catman.ui.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.usecase.category.CategoryWithPurposeGettingUseCase
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.categorylist.model.toCategoryListItems
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val getCategoryWithPurpose: CategoryWithPurposeGettingUseCase,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    private val exceptionHandler: CoroutineExceptionHandler
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Init)
    var state: StateFlow<State> = _state.asStateFlow()

    init {
        initState()
    }

    private fun initState() {
        viewModelScope.launch(exceptionHandler + defaultDispatcher) {
            loadCategoriesWithPurposes().collectLatest {
                _state.value = State.Loaded(it)
            }
        }
    }

    private fun loadCategoriesWithPurposes() =
        getCategoryWithPurpose.all()
            .onStart { Timber.d("start loading categories with purposes") }
            .onEach(this::handleError)
            .mapNotNull {
                Timber.d("map to category list items ${it.getOrNull()?.size}")
                it.getOrNull()?.toCategoryListItems()
            }
            .flowOn(defaultDispatcher)

    private fun <T> handleError(result: Result<T>) {
        if (result.isFailure) {
            _state.value = State.Error(R.string.internal_error)
            throw CancellationException()
        }
    }

    sealed class State {
        object Init : State()
        data class Error(val error: Int) : State()
        data class Loaded(val data: List<CategoryListItem>) : State()
    }
}
