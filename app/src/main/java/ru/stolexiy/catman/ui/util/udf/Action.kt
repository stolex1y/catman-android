package ru.stolexiy.catman.ui.util.udf

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Action<S : State> {
    protected val _state: MutableStateFlow<Status> = MutableStateFlow(Status.Created)

    open val canCancel: Boolean = false
    open val canRevert: Boolean = false

    val state: StateFlow<Status> = _state.asStateFlow()

    /**
     * @return true if canceling has started, false - otherwise
     */
    open suspend fun cancel(): Boolean {
        return false
    }

    /**
     * @return true if reverting has started, false - otherwise
     */
    open suspend fun revert(): Boolean {
        return false
    }

    abstract suspend operator fun invoke(): Flow<S>

    sealed interface Status {
        val isFinished: Boolean

        object Created : Status {
            override val isFinished: Boolean = false
        }

        object Success : Status {
            override val isFinished: Boolean = true
        }

        object Error : Status {
            override val isFinished: Boolean = true
        }

        object Running : Status {
            override val isFinished: Boolean = false
        }

        object Canceled : Status {
            override val isFinished: Boolean = true
        }
    }
}
