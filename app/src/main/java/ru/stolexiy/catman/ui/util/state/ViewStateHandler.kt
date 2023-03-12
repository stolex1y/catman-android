package ru.stolexiy.catman.ui.util.state

import kotlinx.coroutines.flow.StateFlow

interface ViewStateHandler<T : ViewState> {

    fun handlingState(): StateFlow<T>
    fun handleState(newState: T)
}