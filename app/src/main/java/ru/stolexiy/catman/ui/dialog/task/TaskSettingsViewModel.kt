package ru.stolexiy.catman.ui.dialog.task

import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.category.CategoryGettingWithPurposesRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingRepository
import ru.stolexiy.catman.ui.dialog.common.model.Category
import ru.stolexiy.catman.ui.dialog.task.model.Purpose
import ru.stolexiy.catman.ui.dialog.task.model.Task
import ru.stolexiy.catman.ui.util.udf.AbstractViewModel
import ru.stolexiy.catman.ui.util.udf.IData
import ru.stolexiy.catman.ui.util.udf.IState
import ru.stolexiy.catman.ui.util.work.WorkUtils.deserialize
import ru.stolexiy.catman.ui.util.work.task.AddTaskWorker
import ru.stolexiy.catman.ui.util.work.task.DeleteTaskWorker
import ru.stolexiy.catman.ui.util.work.task.UpdateTaskWorker
import ru.stolexiy.common.FlowExtensions.combineResults
import ru.stolexiy.common.FlowExtensions.mapLatestResult
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider


class TaskSettingsViewModel @AssistedInject constructor(
    private val getTask: TaskGettingRepository,
    private val getCategoryWithPurposes: CategoryGettingWithPurposesRepository,
    workManager: Provider<WorkManager>,
    @Named(CoroutineModule.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted private val taskId: Long? = null
) : AbstractViewModel<TaskSettingsDialogEvent, TaskSettingsViewModel.Data, TaskSettingsViewModel.State>(
    Data(),
    stateProducer,
    applicationScope,
    workManager,
    savedStateHandle
) {
    override fun onCleared() {
        super.onCleared()
        Timber.d("cleared")
    }

    override fun dispatchEvent(event: TaskSettingsDialogEvent) {
        when (event) {
            is TaskSettingsDialogEvent.Add -> addTask(event.task)
            TaskSettingsDialogEvent.Cancel -> cancelCurrentWork()
            TaskSettingsDialogEvent.DeleteAdded -> deleteAddedTask()
            TaskSettingsDialogEvent.Load -> startLoadingData()
            TaskSettingsDialogEvent.RevertUpdate -> revertTaskUpdate()
            is TaskSettingsDialogEvent.Update -> updateTask(event.task)
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        val categoriesWithPurposes = getCategoriesWithPurposes()
        return if (taskId != null)
            categoriesWithPurposes.combineResults(getTask()).mapLatestResult(::Data)
        else
            categoriesWithPurposes.mapLatestResult(::Data)
    }

    private fun addTask(task: Task) {
        if (task.isNotValid) {
            updateState(IllegalStateException("Task isn't valid: $task"))
            return
        }
        updateState(State.Adding)
        val workRequest = AddTaskWorker.createWorkRequest(task.toDomainTask())
        startWork(workRequest, State.Added, State.Canceled)
    }

    private fun updateTask(task: Task) {
        if (task.isNotValid) {
            updateState(IllegalStateException("Task isn't valid: $task"))
            return
        }
        updateState(State.Updating)
        val workRequest = UpdateTaskWorker.createWorkRequest(task.toDomainTask())
        startWork(workRequest, State.Updated, State.Canceled)
    }

    private fun deleteAddedTask() {
        val addWork = lastFinishedWork
        if (addWork == null) {
            updateState(
                IllegalStateException("Last finished work is null. Can't revert adding result.")
            )
            return
        }

        val addedTaskId = addWork.outputData.deserialize(Long::class) ?: return

        updateState(State.Deleting)
        val workRequest = DeleteTaskWorker.createWorkRequest(addedTaskId)
        startWork(workRequest, State.Deleted, State.Canceled)
    }

    private fun revertTaskUpdate() {
        val addWork = lastFinishedWork
        if (addWork == null) {
            updateState(
                IllegalStateException("Last finished work is null. Can't revert adding result.")
            )
            return
        }

        val taskUntilUpdate = addWork.outputData.deserialize(DomainTask::class) ?: return

        updateState(State.Canceling)
        val workRequest = UpdateTaskWorker.createWorkRequest(taskUntilUpdate)
        startWork(workRequest, State.Canceled)
    }

    private fun getCategoriesWithPurposes(): Flow<Result<Map<Category, List<Purpose>>>> {
        TODO()
//        return getCategoryWithPurposes.all()
//            .mapResultToMap(DomainCategory::toCategory, DomainPurpose::toPurpose)
    }

    private fun getTask(): Flow<Result<Task?>> {
        requireNotNull(taskId)
        TODO()
//        return getTask.byId(taskId).mapLatestResult { it?.toTask() }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle, taskId: Long?): TaskSettingsViewModel
    }

    sealed interface State : IState {
        object Init : State
        object Loaded : State
        class Error(val error: Int) : State
        object Adding : State
        object Added : State
        object Deleting : State
        object Updating : State
        object Canceling : State
        object Updated : State
        object Deleted : State
        object Canceled : State
    }

    data class Data(
        val categoriesWithPurposes: Map<Category, List<Purpose>> = emptyMap(),
        val editingTask: Task? = null
    ) : IData {
        constructor(pair: Pair<Map<Category, List<Purpose>>, Task?>) : this(pair.first, pair.second)
    }

    companion object {
        val stateProducer: IState.Producer<State> = object : IState.Producer<State> {
            override val initState: State = State.Init
            override val loadedState: State = State.Loaded

            override fun errorState(error: Int): State = State.Error(error)
        }
    }
}
