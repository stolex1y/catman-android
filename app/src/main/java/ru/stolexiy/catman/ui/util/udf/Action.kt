package ru.stolexiy.catman.ui.util.udf

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import java.io.Serializable

interface Action<S : State> : Serializable {
    suspend operator fun invoke(): Flow<S>
    fun cancel() { throw NotImplementedError("This action isn't cancelable") }
}

fun <S : State> Flow<Action<S>>.toState(): Flow<S> = this.flatMapMerge { action ->
    action()
}