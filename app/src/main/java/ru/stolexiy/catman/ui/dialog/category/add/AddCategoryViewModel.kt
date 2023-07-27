package ru.stolexiy.catman.ui.dialog.category.add

import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.usecase.color.ColorGettingUseCase
import ru.stolexiy.catman.ui.dialog.category.model.Category
import ru.stolexiy.catman.ui.dialog.category.model.Color
import ru.stolexiy.catman.ui.util.di.FactoryWithSavedStateHandle
import ru.stolexiy.catman.ui.util.udf.AbstractViewModel
import ru.stolexiy.catman.ui.util.udf.IData
import ru.stolexiy.catman.ui.util.udf.IState
import ru.stolexiy.catman.ui.util.work.WorkUtils.deserialize
import ru.stolexiy.catman.ui.util.work.category.AddCategoryWorker
import ru.stolexiy.catman.ui.util.work.category.DeleteCategoryWorker
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider

class AddCategoryViewModel @AssistedInject constructor(
    private val getColor: ColorGettingUseCase,
    workManager: Provider<WorkManager>,
    @Named(CoroutineModule.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    @Assisted savedStateHandle: SavedStateHandle
) : AbstractViewModel<AddCategoryEvent, AddCategoryViewModel.Data, AddCategoryViewModel.State>(
    Data.EMPTY,
    stateProducer,
    applicationScope,
    workManager,
    savedStateHandle
) {
    override fun onCleared() {
        super.onCleared()
        Timber.d("cleared")
    }

    override fun dispatchEvent(event: AddCategoryEvent) {
        when (event) {
            is AddCategoryEvent.Load -> startLoadingData()
            is AddCategoryEvent.Add -> addCategory(event.category)
            is AddCategoryEvent.Cancel -> cancelCurrentWork()
            is AddCategoryEvent.DeleteAdded -> deleteAddedCategory()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return getColors()
    }

    private fun addCategory(category: Category) {
        if (!category.isValid) {
            updateState(IllegalStateException("Category isn't valid: $category"))
            return
        }
        updateState(State.Adding)
        val workRequest = AddCategoryWorker.createWorkRequest(category.toDomainCategory())
        startWork(workRequest, State.Added, State.Canceled)
    }

    private fun deleteAddedCategory() {
        val addWork = lastFinishedWork
        if (addWork == null) {
            updateState(
                IllegalStateException("Last finished work is null. Can't revert adding result.")
            )
            return
        }

        val addedCategoryId = addWork.outputData.deserialize(Long::class) ?: return

        updateState(State.Deleting)
        val workRequest = DeleteCategoryWorker.createWorkRequest(addedCategoryId)
        startWork(workRequest, State.Deleted, State.Canceled)
    }

    private fun getColors(): Flow<Result<Data>> {
        return getColor.all()
            .map { result ->
                result.map { domainColors ->
                    Data(domainColors.map { Color.fromDomainColor(it) })
                }
            }
    }

    @AssistedFactory
    interface Factory : FactoryWithSavedStateHandle<AddCategoryViewModel>

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
        val colors: List<Color>
    ) : IData {
        companion object {
            @JvmStatic
            val EMPTY: Data = Data(emptyList())
        }
    }

    companion object {
        val stateProducer: IState.Producer<State> = object : IState.Producer<State> {
            override val initState: State = State.Init
            override val loadedState: State = State.Loaded

            override fun errorState(error: Int): State = State.Error(error)
        }
    }
}

