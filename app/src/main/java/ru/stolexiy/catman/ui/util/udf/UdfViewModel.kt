/*
package ru.stolexiy.catman.ui.util.udf

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val EVENTS_LIMIT = 16

abstract class UdfViewModel<E : Event<A, S>, A : Action<S>, S : State>(
    protected val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val events: Channel<E> = Channel(EVENTS_LIMIT)

    private var lastAction: A? = null
    private var currentAction: A? = null

    protected abstract val initialState: S
    private val _state: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }
    val state: StateFlow<S> by lazy { _state.asStateFlow() }

    init {
        startEventsProcessing()
    }

    fun dispatch(event: E) {
        viewModelScope.launch { events.send(event) }
    }

    fun cancelCurrentAction() {
        viewModelScope.launch { currentAction?.cancel() }
    }

    protected fun revertLastAction() {
        viewModelScope.launch { lastAction?.revert() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startEventsProcessing() {
        viewModelScope.launch {
            events.consumeAsFlow()
                .map { it.toAction() }
                .onEach { newAction ->
                    launch { updateCurrentAction(newAction) }.join()
                }
                .flatMapConcat { it.invoke() }
                .distinctUntilChanged()
                .collectLatest {
                    _state.value = it
                }
        }
    }

    private suspend fun updateCurrentAction(newAction: A) {
        currentAction?.state?.collectLatest { actionStatus ->
            if (actionStatus.isFinished) {
                lastAction = currentAction
                currentAction = newAction
                throw CancellationException()
            }
        }
    }
}
*/
