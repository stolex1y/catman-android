package ru.stolexiy.catman.ui.util.udf

sealed class SimpleLoadingState<T> : State {
    class Loading<T> : SimpleLoadingState<T>()

    class Complete<T : Any?>(
        val result: T
    ) : SimpleLoadingState<T>()

    class Error<T>(
        val throwable: Throwable
    ) : SimpleLoadingState<T>()
}
