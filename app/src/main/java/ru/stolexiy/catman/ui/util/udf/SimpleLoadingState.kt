package ru.stolexiy.catman.ui.util.udf

sealed interface SimpleLoadingState : State {
    object Init : SimpleLoadingState
    object Loading : SimpleLoadingState
    object Loaded : SimpleLoadingState
    data class Error(val error: Int) : SimpleLoadingState
}