package ru.stolexiy.catman.ui.util.udf

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

private const val EVENTS_LIMIT = 128

abstract class UdfViewModel<A : Action<S>, S : State>(
    protected val savedStateHandle: SavedStateHandle
) : ViewModel() {
    protected abstract val mInitialState: S
    protected open val mEvents: Channel<E> = Channel(EVENTS_LIMIT)

    private val mState: MutableStateFlow<S> by lazy { MutableStateFlow(mInitialState) }
    val state: StateFlow<S> by lazy { mState.asStateFlow() }

    private var mCurrentAction: A? = null

    init {
        startEventsProcessing()
    }

    fun dispatch(event: E) {
        viewModelScope.launch { mEvents.send(event) }
    }

    fun cancelCurrentAction() {
        viewModelScope.launch { mCurrentAction?.cancel() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startEventsProcessing() {
        viewModelScope.launch {
            mEvents.consumeAsFlow()
                .map { it.toAction() }
                .onEach { mCurrentAction = it }
                .flatMapConcat { it.invoke() }
                .distinctUntilChanged()
                .collectLatest {
                    mState.value = it
                }
        }
    }
}
