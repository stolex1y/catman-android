package ru.stolexiy.catman.ui.timer

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.ui.util.di.FactoryWithSavedStateHandle
import ru.stolexiy.catman.ui.util.udf.AbstractViewModel
import ru.stolexiy.catman.ui.util.udf.IData
import ru.stolexiy.catman.ui.util.udf.IState
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Named
import javax.inject.Provider

class TimerViewModel @AssistedInject constructor(
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    workManager: Provider<WorkManager>,
    @Named(CoroutineModule.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    @Assisted savedStateHandle: SavedStateHandle
) : AbstractViewModel<TimerFragmentEvent, TimerViewModel.Data, TimerViewModel.State>(
    Data(),
    stateProducer,
    applicationScope,
    workManager,
    savedStateHandle
) {

    override fun loadData(): Flow<Result<Data>> {
        return flow {
            emit(runCatching {
                Data()
            })
        }
    }

    override fun dispatchEvent(event: TimerFragmentEvent) {
        TODO("Not yet implemented")
    }

    private fun addTimeToTask(taskId: Long) {
        TODO("Add time to task. Also need the timer to get init time")
    }

    companion object {
        val stateProducer: IState.Producer<State> = object : IState.Producer<State> {
            override val initState: State = State.Init
            override val loadedState: State = State.Loaded

            override fun errorState(error: Int): State {
                return State.Error(error)
            }
        }
    }

    sealed interface State : IState {
        object Init : State
        data class Error(@StringRes val error: Int) : State
        object Loaded : State
    }

    class Data : IData

    @AssistedFactory
    interface Factory : FactoryWithSavedStateHandle<TimerViewModel>
}
