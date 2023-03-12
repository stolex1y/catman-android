package ru.stolexiy.catman.ui.util.state

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class StatefulViewModel<T : ViewState>(
    initState: T,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    protected val _state: MutableStateFlow<T> = MutableStateFlow(initState)
    val state: StateFlow<T> = _state.asStateFlow()


    override fun onCleared() {
        super.onCleared()
        savedStateHandle[SAVED_STATE] = bundleToSave()
    }

    protected abstract fun bundleToSave(): Bundle
    protected fun restoredBundle(): Bundle? = savedStateHandle.get<Bundle>(SAVED_STATE)

    companion object {
        private val SAVED_STATE = "SAVED_STATE"
    }
}
