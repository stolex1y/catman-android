package ru.stolexiy.catman.ui.util.udf

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.ui.util.work.WorkUtils.deserialize
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider
import kotlin.coroutines.cancellation.CancellationException

abstract class AbstractViewModel<E : IEvent, D : IData, S : IState>(
    initData: D,
    private val stateProducer: IState.Producer<S>,
    @Named(CoroutineModule.APPLICATION_SCOPE) protected val applicationScope: CoroutineScope,
    private val workManager: Provider<WorkManager>,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var dataLoadingJob: Job? = null

    private val _state: MutableStateFlow<S> = MutableStateFlow(stateProducer.initState)
    val state: StateFlow<S> = _state.asStateFlow()
    var prevState: S = stateProducer.initState
        private set

    private val _data: MutableStateFlow<D> = MutableStateFlow(initData)
    val data: StateFlow<D> by lazy {
        if (dataLoadingJob == null) {
            Timber.w("Didn't start data loading (call startLoadingData in ${this::class.simpleName})")
        }
        _data.asStateFlow()
    }

    private var work: StateFlow<WorkInfo>? = null
    protected var lastFinishedWork: WorkInfo? = null
        private set

    abstract fun dispatchEvent(event: E)

    protected abstract fun loadData(): Flow<Result<D>>

    private fun setErrorStateWith(@StringRes errorMsg: Int) {
        updateState(stateProducer.errorState(errorMsg))
    }

    protected fun startLoadingData() {
        val currentLoadingJob = dataLoadingJob
        if (currentLoadingJob != null) {
            currentLoadingJob.cancel()
            dataLoadingJob = null
        }
        dataLoadingJob = viewModelScope.launch {
            loadData().handleError()
                .mapNotNull { it.getOrNull() }
                .collectLatest {
                    _data.value = it
                    if (state.value == stateProducer.initState)
                        _state.value = stateProducer.loadedState
                }
        }
    }

    @StringRes
    protected open fun parseError(error: Throwable): Int {
        return when (error) {
            else -> R.string.internal_error
        }
    }

    protected fun cancelCurrentWork() {
        val currentWork = work?.value ?: return
        workManager.get().cancelWorkById(currentWork.id)
    }

    protected fun updateState(state: S) {
        prevState = _state.value
        _state.value = state
    }

    protected fun updateState(error: Throwable) {
        Timber.e(error, "Updated state with error:")
        setErrorStateWith(parseError(error))
    }

    protected fun startWork(
        workRequest: WorkRequest,
        finishState: S,
        canceledState: S? = null
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
                when (finishedWorkInfo.state) {
                    WorkInfo.State.FAILED ->
                        updateState(finishedWorkInfo.outputData.deserialize(Throwable::class)!!)

                    WorkInfo.State.CANCELLED ->
                        updateState(canceledState ?: finishState)

                    else ->
                        updateState(finishState)
                }
                updateState(stateProducer.loadedState)
                lastFinishedWork = finishedWorkInfo
            }
        }
    }

    private fun <T> Flow<Result<T>>.handleError(): Flow<Result<T>> {
        return this.onEach {
            if (it.isFailure) {
                Timber.e(it.exceptionOrNull()!!, "error in flow")
                updateState(it.exceptionOrNull()!!)
                throw CancellationException()
            }
        }
    }
}
