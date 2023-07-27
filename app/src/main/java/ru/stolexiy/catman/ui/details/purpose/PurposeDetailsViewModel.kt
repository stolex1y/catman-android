package ru.stolexiy.catman.ui.details.purpose

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingByPurposeRepository
import ru.stolexiy.catman.ui.details.purpose.model.Purpose
import ru.stolexiy.catman.ui.details.purpose.model.Task
import ru.stolexiy.catman.ui.details.purpose.model.toCategory
import ru.stolexiy.catman.ui.details.purpose.model.toPurpose
import ru.stolexiy.catman.ui.details.purpose.model.toTask
import ru.stolexiy.catman.ui.util.udf.AbstractViewModel
import ru.stolexiy.catman.ui.util.udf.IData
import ru.stolexiy.catman.ui.util.udf.IState
import ru.stolexiy.common.FlowExtensions.combineResultsTransform
import ru.stolexiy.common.FlowExtensions.mapLatestResult
import ru.stolexiy.common.Mappers.flatMapLatestResult
import ru.stolexiy.common.Mappers.mapLatestResultList
import ru.stolexiy.common.Mappers.requireNotNullResult
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Named
import javax.inject.Provider

class PurposeDetailsViewModel @AssistedInject constructor(
    private val categoryGet: CategoryGettingRepository,
    private val purposeGet: PurposeGettingRepository,
    private val taskByPurposeGet: TaskGettingByPurposeRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    workManager: Provider<WorkManager>,
    @Named(CoroutineModule.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    @Assisted private val purposeId: Long,
    @Assisted savedStateHandle: SavedStateHandle
) : AbstractViewModel<PurposeDetailsEvent, PurposeDetailsViewModel.Data, PurposeDetailsViewModel.State>(
    Data.EMPTY,
    stateProducer,
    applicationScope,
    workManager,
    savedStateHandle,
) {

    override fun dispatchEvent(event: PurposeDetailsEvent) {
        when (event) {
            PurposeDetailsEvent.Cancel -> TODO()
            is PurposeDetailsEvent.MarkFinished -> TODO()
            is PurposeDetailsEvent.MarkNotFinished -> TODO()
            is PurposeDetailsEvent.SwapPriority -> TODO()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        val domainPurposeFlow = purposeGet.byId(purposeId).requireNotNullResult()
        val categoryFlow = domainPurposeFlow
            .flatMapLatestResult { categoryGet.byId(it.id) }
            .requireNotNullResult()
            .mapLatestResult(DomainCategory::toCategory)
        val purposeFlow =
            domainPurposeFlow.combineResultsTransform(categoryFlow) { domainPurpose, category ->
                domainPurpose.toPurpose(category)
            }
        val tasksFlow = purposeFlow.flatMapLatestResult {
            taskByPurposeGet.all(it.id)
        }.mapLatestResultList(DomainTask::toTask)
        return purposeFlow.combineResultsTransform(tasksFlow) { p, t -> Data(p, t) }
    }

    companion object {
        private val stateProducer: IState.Producer<State> = object : IState.Producer<State> {
            override val initState: State = State.Init
            override val loadedState: State = State.Loaded

            override fun errorState(@StringRes error: Int): State {
                return State.Error(error)
            }
        }
    }

    sealed interface State : IState {
        object Init : State
        data class Error(@StringRes val error: Int) : State
        object Loaded : State
        object Canceled : State
    }

    data class Data(
        val purpose: Purpose,
        val tasks: List<Task>
    ) : IData {
        companion object {
            val EMPTY by lazy {
                Data(
                    purpose = Purpose(),
                    tasks = emptyList(),
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(purposeId: Long, savedStateHandle: SavedStateHandle): PurposeDetailsViewModel
    }
}