package ru.stolexiy.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

object FlowExtensions {
    fun <T> Flow<T>.mapToResult() =
        this.map { Result.success(it) }.catch {
            emit(Result.failure(it))
        }

    fun <T1, T2> Flow<Result<T1>>.combineResults(flow: Flow<Result<T2>>): Flow<Result<Pair<T1, T2>>> {
        return combine(flow) { first, second ->
            if (first.isFailure)
                Result.failure(first.exceptionOrNull()!!)
            else if (second.isFailure)
                Result.failure(second.exceptionOrNull()!!)
            else
                Result.success(first.getOrNull()!! to second.getOrNull()!!)
        }
    }

    fun <T1, T2, R> Flow<Result<T1>>.combineResultsTransform(
        flow: Flow<Result<T2>>,
        transform: suspend (T1, T2) -> R
    ): Flow<Result<R>> {
        return combineTransform(flow) { first: Result<T1>, second: Result<T2> ->
            if (first.isFailure)
                Result.failure(first.exceptionOrNull()!!)
            else if (second.isFailure)
                Result.failure(second.exceptionOrNull()!!)
            else
                Result.success(transform(first.getOrNull()!!, second.getOrNull()!!))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T, R> Flow<Result<T>>.mapLatestResult(transform: suspend (T) -> R): Flow<Result<R>> {
        return mapLatest { result: Result<T> ->
            result.map { transform(it) }
        }
    }
}
