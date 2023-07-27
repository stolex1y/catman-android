package ru.stolexiy.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.stolexiy.common.FlowExtensions.mapLatestResult

object Mappers {
    inline fun <E, reified D> Array<E>.mapToArray(transform: E.() -> D): Array<D> {
        return map { it: E -> it.transform() }.toTypedArray()
    }

    fun <E1, E2, D1, D2> Map<E1, List<E2>>.mapToMap(
        transform1: E1.() -> D1,
        transform2: E2.() -> D2
    ): Map<D1, List<D2>> {
        val domainMap = mutableMapOf<D1, List<D2>>()
        this.forEach { mapEntry ->
            val e1 = mapEntry.key
            val e2List = mapEntry.value
            domainMap[e1.transform1()] = e2List.map { it: E2 -> it.transform2() }
        }
        return domainMap
    }

    fun <E1, E2, D1, D2> Map<E1, List<E2>>.mapToMap(
        transform1: E1.(List<E2>) -> D1,
        transform2: E2.(D1) -> D2
    ): Map<D1, List<D2>> {
        val domainMap = mutableMapOf<D1, List<D2>>()
        this.forEach { mapEntry ->
            val e1 = mapEntry.key
            val e2List = mapEntry.value
            val d1 = e1.transform1(e2List)
            domainMap[d1] = e2List.map { it: E2 -> it.transform2(d1) }
        }
        return domainMap
    }

    fun <E1, E2, D1, D2> Flow<Map<E1, List<E2>>>.mapToMap(
        transform1: E1.() -> D1,
        transform2: E2.() -> D2
    ): Flow<Map<D1, List<D2>>> {
        return map { it.mapToMap(transform1, transform2) }
    }

    fun <E1, E2, D1, D2> Flow<Map<E1, List<E2>>>.mapToMap(
        transform1: E1.(List<E2>) -> D1,
        transform2: E2.(D1) -> D2
    ): Flow<Map<D1, List<D2>>> {
        return map { it.mapToMap(transform1, transform2) }
    }

    fun <E1, E2, D1, D2> Flow<Result<Map<E1, List<E2>>>>.mapResultToMap(
        transform1: E1.() -> D1,
        transform2: E2.() -> D2
    ): Flow<Result<Map<D1, List<D2>>>> {
        return mapLatestResult { it.mapToMap(transform1, transform2) }
    }

    fun <E1, E2, D1, D2> Flow<Result<Map<E1, List<E2>>>>.mapResultToMap(
        transform1: E1.(List<E2>) -> D1,
        transform2: E2.(D1) -> D2
    ): Flow<Result<Map<D1, List<D2>>>> {
        return mapLatestResult { it.mapToMap(transform1, transform2) }
    }

    fun <T> Flow<Result<T?>>.requireNotNullValidResult(
        ifNullValue: Throwable = IllegalArgumentException()
    ): Flow<T> {
        return map { it.getOrThrow() ?: throw ifNullValue }
    }

    fun <T> Flow<Result<T?>>.requireNotNullResult(
        ifNullValue: Throwable = IllegalArgumentException()
    ): Flow<Result<T>> {
        return map {
            if (it.isFailure)
                Result.failure(it.exceptionOrNull()!!)
            else if (it.getOrNull() == null)
                Result.failure(ifNullValue)
            else
                Result.success(it.getOrNull()!!)
        }
    }

    fun <T> Result<T?>.requireNotNullValidResult(
        ifNullValue: Throwable = IllegalArgumentException()
    ): T {
        return getOrThrow() ?: throw ifNullValue
    }

    fun <T1, T2> Flow<List<T1>>.mapList(transform: suspend T1.() -> T2): Flow<List<T2>> {
        return map { list -> list.map { it.transform() } }
    }

    fun <T1, T2> Flow<Result<List<T1>>>.mapLatestResultList(transform: suspend T1.() -> T2): Flow<Result<List<T2>>> {
        return mapLatestResult { list -> list.map { it.transform() } }
    }

    fun <T, R> Flow<Result<T>>.flatMapLatestResult(transform: suspend (value: T) -> Flow<Result<R>>): Flow<Result<R>> {
        val flowOfFlow = mapLatestResult {
            transform(it)
        }
        return flow {
            flowOfFlow.collectLatest {
                if (it.isFailure)
                    emit(Result.failure(it.exceptionOrNull()!!))
                else
                    emitAll(it.getOrNull()!!)
            }
        }
    }
}
