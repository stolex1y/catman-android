package ru.stolexiy.catman.domain.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.toResult() =
    this.map { Result.success(it) }.catch {
        if (it is CancellationException)
            throw it
        emit(Result.failure(it))
    }

fun <T> Result<T>.cancellationTransparency() =
    this.onFailure {
        if (it is CancellationException)
            throw it
    }