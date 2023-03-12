package ru.stolexiy.catman.ui.util.udf

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge

interface Event<A : Action<S>, S : State> {
    suspend fun toAction(): Flow<A>

}

fun <A : Action<S>, S : State> Flow<Event<A, S>>.toAction(): Flow<A> = this.flatMapMerge { event ->
    event.toAction()
}
