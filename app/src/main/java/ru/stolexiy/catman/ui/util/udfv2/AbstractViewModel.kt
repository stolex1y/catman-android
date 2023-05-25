package ru.stolexiy.catman.ui.util.udfv2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
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
import kotlinx.coroutines.launch
import ru.stolexiy.catman.core.di.CoroutineModule
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider
import kotlin.coroutines.cancellation.CancellationException

abstract class AbstractViewModel<E : IEvent, D : IData, S : IState>(
    initData: D,
    initState: S,
    @Named(CoroutineModule.APPLICATION_SCOPE) private val applicationScope: CoroutineScope,
    private val workManager: Provider<WorkManager>,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    protected val _state: MutableStateFlow<S> = MutableStateFlow(initState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _data: MutableStateFlow<D> = MutableStateFlow(initData)
    val data: StateFlow<D> = _data.asStateFlow()

    private var work: StateFlow<WorkInfo>? = null
    protected var lastFinishedWork: WorkInfo? = null
        private set

    protected abstract val loadedState: S

    init {
        viewModelScope.launch {
            loadData().handleError()
                .mapNotNull { it.getOrNull() }
                .collectLatest {
                    _data.value = it
                    if (state.value == initState)
                        _state.value = loadedState
                }
        }
    }

    abstract fun dispatchEvent(event: E)

    protected abstract fun loadData(): Flow<Result<D>>

    protected abstract fun parseError(error: Throwable): S

    protected fun cancelCurrentWork() {
        val currentWork = work?.value ?: return
        workManager.get().cancelWorkById(currentWork.id)
    }

    protected fun startWork(
        workRequest: WorkRequest,
        finishState: S,
        errorStateProvider: () -> S
    ) {
        workManager.get().run {
            enqueue(workRequest)
            applicationScope.launch {
                val currentWork = getWorkInfoByIdLiveData(workRequest.id)
                    .asFlow()
                    .stateIn(applicationScope)
                work = currentWork

                currentWork.takeWhile { !it.state.isFinished }.collect()
                val finishedWorkInfo = currentWork.value
                _state.value = if (finishedWorkInfo.state == WorkInfo.State.FAILED)
                    errorStateProvider()
                else
                    finishState
                lastFinishedWork = finishedWorkInfo
            }
        }
    }

    private fun <T> Flow<Result<T>>.handleError(): Flow<Result<T>> {
        return this.onEach {
            if (it.isFailure) {
                Timber.e(it.exceptionOrNull())
                _state.value = parseError(it.exceptionOrNull()!!)
                throw CancellationException()
            }
        }
    }
}
