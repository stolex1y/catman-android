package ru.stolexiy.catman.ui.dialog.purpose.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.usecase.category.CategoryGettingUseCase
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.purpose.model.toCategory
import ru.stolexiy.catman.ui.util.work.WorkUtils.getResult
import ru.stolexiy.catman.ui.util.work.purpose.AddPurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker
import timber.log.Timber
import javax.inject.Named

class AddPurposeViewModel @AssistedInject constructor(
    @Named(CoroutineModule.APPLICATION_SCOPE) private val applicationScope: CoroutineScope,
    private val getCategory: CategoryGettingUseCase,
    private val workManager: WorkManager,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Init)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _data: MutableStateFlow<Data> = MutableStateFlow(Data.EMPTY)
    val data: StateFlow<Data> = _data.asStateFlow()

    private var work: StateFlow<WorkInfo>? = null
    private var lastFinishedWork: WorkInfo? = null

    init {
        initState()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("cleared")
    }

    fun dispatchEvent(event: AddPurposeEvent) {
        when (event) {
            is AddPurposeEvent.Add -> addPurpose(event.purpose)
            is AddPurposeEvent.Cancel -> cancelCurrentWork()
            is AddPurposeEvent.DeleteAdded -> deleteAddedPurpose()
        }
    }

    private fun addPurpose(purpose: Purpose) {
        if (!purpose.isValid || _state.value != State.Loaded) {
            _state.value = State.Error(R.string.internal_error)
            return
        }
        _state.value = State.Adding
        val workRequest = AddPurposeWorker.createWorkRequest(purpose.toDomainPurpose())
        startWork(workRequest, State.Added)
    }

    private fun deleteAddedPurpose() {
        val addWork = lastFinishedWork
        if (addWork == null) {
            _state.value = State.Error(R.string.internal_error)
            return
        }

        _state.value = State.Deleting
        val addedPurposeId = addWork.outputData.getResult<Long>()
        val workRequest = DeletePurposeWorker.createWorkRequest(addedPurposeId)
        startWork(workRequest, State.Deleted)
    }

    private fun cancelCurrentWork() {
        val currentWork = work?.value ?: return
        workManager.cancelWorkById(currentWork.id)
    }

    private fun startWork(workRequest: WorkRequest, finishState: State) {
        workManager.run {
            enqueue(workRequest)
            applicationScope.launch {
                val currentWork = getWorkInfoByIdLiveData(workRequest.id)
                    .asFlow()
                    .stateIn(applicationScope)
                work = currentWork

                currentWork.takeWhile { !it.state.isFinished }.collect()
                val finishedWorkInfo = currentWork.value
                _state.value = if (finishedWorkInfo.state == WorkInfo.State.FAILED)
                    State.Error(R.string.internal_error)
                else
                    finishState
                lastFinishedWork = finishedWorkInfo
            }
        }
    }

    private fun initState() {
        viewModelScope.launch {
            getCategories().collectLatest { categories ->
                _data.update {
                    it.copy(categories = categories)
                }
                _state.value = State.Loaded
            }
        }
    }

    private fun getCategories() =
        getCategory.all()
            .handleError()
            .mapNotNull {
                it.getOrNull()?.map(DomainCategory::toCategory)
            }

    private fun <T> Flow<Result<T>>.handleError(): Flow<Result<T>> {
        return this.onEach {
            if (it.isFailure) {
                _state.value = State.Error(R.string.internal_error)
                throw CancellationException()
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): AddPurposeViewModel
    }

    sealed class State {
        object Init : State()
        object Loaded : State()
        data class Error(val error: Int) : State()
        object Adding : State()
        object Added : State()
        object Deleting : State()
        object Deleted : State()
    }

    data class Data(
        val categories: List<Category>
    ) {
        companion object {
            @JvmStatic
            val EMPTY: Data by lazy { Data(emptyList()) }
        }
    }
}
