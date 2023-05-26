package ru.stolexiy.catman.ui.dialog.purpose.add

import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.usecase.category.CategoryGettingUseCase
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.purpose.model.toCategory
import ru.stolexiy.catman.ui.util.udfv2.AbstractViewModel
import ru.stolexiy.catman.ui.util.udfv2.IData
import ru.stolexiy.catman.ui.util.udfv2.IState
import ru.stolexiy.catman.ui.util.work.WorkUtils.deserialize
import ru.stolexiy.catman.ui.util.work.purpose.AddPurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider

class AddPurposeViewModel @AssistedInject constructor(
    private val getCategory: CategoryGettingUseCase,
    workManager: Provider<WorkManager>,
    @Named(CoroutineModule.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    @Assisted savedStateHandle: SavedStateHandle
) : AbstractViewModel<AddPurposeEvent, AddPurposeViewModel.Data, AddPurposeViewModel.State>(
    Data.EMPTY,
    State.Init,
    applicationScope,
    workManager,
    savedStateHandle
) {

    override val loadedState: State = State.Loaded

    init {
        startLoadingData()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("cleared")
    }

    override fun dispatchEvent(event: AddPurposeEvent) {
        when (event) {
            is AddPurposeEvent.Load -> startLoadingData()
            is AddPurposeEvent.Add -> addPurpose(event.purpose)
            is AddPurposeEvent.Cancel -> cancelCurrentWork()
            is AddPurposeEvent.DeleteAdded -> deleteAddedPurpose()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return getCategories()
    }

    override fun setErrorStateWith(errorMsg: Int) {
        updateState(State.Error(errorMsg))
    }

    private fun addPurpose(purpose: Purpose) {
        if (!purpose.isValid) {
            updateState(IllegalStateException("Purpose isn't valid: $purpose"))
            return
        }
        updateState(State.Adding)
        val workRequest = AddPurposeWorker.createWorkRequest(purpose.toDomainPurpose())
        startWork(workRequest, State.Added, State.Canceled)
    }

    private fun deleteAddedPurpose() {
        val addWork = lastFinishedWork
        if (addWork == null) {
            updateState(
                IllegalStateException("Last finished work is null. Can't revert adding result.")
            )
            return
        }

        val addedPurposeId = addWork.outputData.deserialize(Long::class) ?: return

        updateState(State.Deleting)
        val workRequest = DeletePurposeWorker.createWorkRequest(addedPurposeId)
        startWork(workRequest, State.Deleted, State.Canceled)
    }

    private fun getCategories(): Flow<Result<Data>> {
        return getCategory.all()
            .map { result ->
                result.map { domainCategories ->
                    Data(domainCategories.map(DomainCategory::toCategory))
                }
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): AddPurposeViewModel
    }

    sealed class State : IState {
        object Init : State()
        object Loaded : State()
        data class Error(val error: Int) : State()
        object Adding : State()
        object Added : State()
        object Deleting : State()
        object Deleted : State()
        object Canceled : State()
    }

    data class Data(
        val categories: List<Category>
    ) : IData {
        companion object {
            @JvmStatic
            val EMPTY: Data = Data(emptyList())
        }
    }
}
