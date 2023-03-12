package ru.stolexiy.catman.ui.util.udf

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class UdfViewModel<A : Action<S>, S : State>(
    protected val savedStateHandle: SavedStateHandle
) : ViewModel() {
    protected abstract val initialState: S
    protected open val actions: Channel<A> = Channel(Channel.UNLIMITED)

    private val mState: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state: StateFlow<S> = mState.asStateFlow()

    private var mCurrentAction: A? = null

    init {
        startEventsProcessing()
    }

    protected fun A.dispatch() {
        viewModelScope.launch { actions.send(this@dispatch) }
    }

    fun cancelCurrentAction() {
        mCurrentAction?.cancel()
    }

    private fun startEventsProcessing() {
        viewModelScope.launch {
            actions.consumeAsFlow()
                .onEach { mCurrentAction = it }
                .toState()
                .distinctUntilChanged()
                .collect {
                    mState.value = it
                }
        }
    }
}