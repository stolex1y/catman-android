package ru.stolexiy.catman.ui.categorylist

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.category.CategoryGettingWithPurposesRepository
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.util.di.FactoryWithSavedStateHandle
import ru.stolexiy.catman.ui.util.udf.AbstractViewModel
import ru.stolexiy.catman.ui.util.udf.IData
import ru.stolexiy.catman.ui.util.udf.IState
import ru.stolexiy.catman.ui.util.work.WorkUtils.deserialize
import ru.stolexiy.catman.ui.util.work.purpose.AddPurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.UpdatePurposesPriorityWorker
import ru.stolexiy.common.FlowExtensions.mapLatestResult
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider

class CategoryListViewModel @AssistedInject constructor(
    private val getCategoryWithPurposes: CategoryGettingWithPurposesRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    workManager: Provider<WorkManager>,
    @Named(CoroutineModule.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    @Assisted savedStateHandle: SavedStateHandle
) : AbstractViewModel<CategoryListEvent, CategoryListViewModel.Data, CategoryListViewModel.State>(
    Data.EMPTY,
    stateProducer,
    applicationScope,
    workManager,
    savedStateHandle
) {

    override fun dispatchEvent(event: CategoryListEvent) {
        when (event) {
            is CategoryListEvent.Load -> startLoadingData()
            is CategoryListEvent.Add -> addPurpose()
            is CategoryListEvent.Delete -> deletePurpose(event.id)
            is CategoryListEvent.UpdatePriorities -> updatePurposesPriority(
                event.list.filterIsInstance<CategoryListItem.PurposeItem>()
            )

            is CategoryListEvent.Cancel -> cancelCurrentWork()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return loadCategoriesWithPurposes()
    }

    private fun loadCategoriesWithPurposes() =
        getCategoryWithPurposes.allOrderedByPurposePriority()
            .onStart { Timber.d("start loading categories with purposes") }
            .mapLatestResult {
                Data(it.toCategoryListItems())
            }
            .flowOn(defaultDispatcher)

    private fun deletePurpose(id: Long) {
        updateState(State.Deleting)
        val workRequest = DeletePurposeWorker.createWorkRequest(id)
        startWork(workRequest, State.Deleted, State.Canceled)
    }

    private fun addPurpose() {
        val deleteWork = lastFinishedWork
        if (deleteWork == null) {
            updateState(
                IllegalStateException("Last finished work is null. Can't revert deleting result.")
            )
            return
        }
        val deletedPurpose = deleteWork.outputData.deserialize(DomainPurpose::class) ?: return
        updateState(State.Adding)
        val workRequest = AddPurposeWorker.createWorkRequest(deletedPurpose)
        startWork(workRequest, State.Added, State.Canceled)
    }

    private fun updatePurposesPriority(purposes: List<CategoryListItem.PurposeItem>) {
        val workRequest = UpdatePurposesPriorityWorker.createWorkRequest(
            purposes.associate { it.id to it.priority }
        )
        startWork(workRequest, State.Loaded, State.Canceled)
    }

    private fun Map<DomainCategory, List<DomainPurpose>>.toCategoryListItems(): List<CategoryListItem> {
        return flatMap {
            listOf(
                it.key.toCategoryItem(),
                *it.value.map(DomainPurpose::toPurposeItem).toTypedArray()
            )
        }
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
        object Deleting : State
        object Deleted : State
        object Adding : State
        object Added : State
        object Canceled : State
    }

    data class Data(
        val categories: List<CategoryListItem> = emptyList()
    ) : IData {
        companion object {
            val EMPTY by lazy {
                Data()
            }
        }
    }

    @AssistedFactory
    interface Factory : FactoryWithSavedStateHandle<CategoryListViewModel>
}

private fun DomainCategory.toCategoryItem() = CategoryListItem.CategoryItem(
    id = id,
    name = name,
    color = color
)

private fun DomainPurpose.toPurposeItem() = CategoryListItem.PurposeItem(
    id = id,
    name = name,
    deadline = deadline,
    isBurning = isDeadlineBurning,
    progress = progress,
    priority = priority,
)
