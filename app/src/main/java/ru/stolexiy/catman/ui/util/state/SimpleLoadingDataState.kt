package ru.stolexiy.catman.ui.util.state

sealed class SimpleLoadingDataState<T> : ViewState {
    class IsLoading<T> : SimpleLoadingDataState<T>()
    class Error<T> : SimpleLoadingDataState<T>()
    class Loaded<T> (val data: T) : SimpleLoadingDataState<T>()
}
