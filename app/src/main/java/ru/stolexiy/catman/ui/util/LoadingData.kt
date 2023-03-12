package ru.stolexiy.catman.ui.util

sealed class LoadingData<T> {
    object Loading : LoadingData<Unit>()
    data class Loaded<T>(val data: T) : LoadingData<T>()
}