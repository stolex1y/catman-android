package ru.stolexiy.catman.ui.categorylist

import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.Lazy
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.category.CategoryWithPurposeGettingUseCase
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.categorylist.model.toCategoryListItems
import ru.stolexiy.catman.ui.util.di.FactoryWithSavedStateHandle
import ru.stolexiy.catman.ui.util.udfv2.AbstractViewModel
import ru.stolexiy.catman.ui.util.udfv2.IData
import ru.stolexiy.catman.ui.util.udfv2.IState
import ru.stolexiy.catman.ui.util.work.WorkUtils.deserialize
import ru.stolexiy.catman.ui.util.work.purpose.AddPurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider

class CategoryListViewModel @AssistedInject constructor(
    private val getCategoryWithPurpose: Lazy<CategoryWithPurposeGettingUseCase>,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    workManager: Provider<WorkManager>,
    @Named(CoroutineModule.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    @Assisted savedStateHandle: SavedStateHandle
) : AbstractViewModel<CategoryListEvent, CategoryListViewModel.Data, CategoryListViewModel.State>(
    Data.EMPTY,
    State.Init,
    applicationScope,
    workManager,
    savedStateHandle
) {

    override val loadedState: State = State.Loaded

    override fun dispatchEvent(event: CategoryListEvent) {
        when (event) {
            is CategoryListEvent.Load -> startLoadingData()
            is CategoryListEvent.Add -> addPurpose()
            is CategoryListEvent.Delete -> deletePurpose(event.id)
            is CategoryListEvent.SwapPriority -> swapPurposesPriority(
                event.firstId,
                event.secondId
            )
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return loadCategoriesWithPurposes()
    }

    override fun setErrorStateWith(errorMsg: Int) {
        updateState(State.Error(errorMsg))
    }

    private fun loadCategoriesWithPurposes() =
        getCategoryWithPurpose.get().allWithPurposeOrderingByPriority()
            .onStart { Timber.d("start loading categories with purposes") }
            .map { result ->
                result.map {
                    Data(it.toCategoryListItems())
                }
            }
            .flowOn(defaultDispatcher)

    private fun deletePurpose(id: Long) {
        updateState(State.Deleting)
        val workRequest = DeletePurposeWorker.createWorkRequest(id)
        startWork(workRequest, State.Deleted)
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
        startWork(workRequest, State.Added)
    }

    private fun swapPurposesPriority(firstId: Long, secondId: Long) {
        val workRequest = SwapPurposePriorityWorker.createWorkRequest(firstId, secondId)
        startWork(workRequest, State.Loaded)
    }

    sealed interface State : IState {
        object Init : State
        data class Error(val error: Int) : State
        object Loaded : State
        object Deleting : State
        object Deleted : State
        object Adding : State
        object Added : State
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
