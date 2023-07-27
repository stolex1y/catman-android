package ru.stolexiy.catman.ui.util.work.task

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.usecase.task.TaskAddingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class AddTaskWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val addingUseCase: TaskAddingUseCase,
) : AbstractWorker<DomainTask, Long>(
    WorkUtils.ADDING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.task_adding),
    "Task adding",
    { action(addingUseCase, it) },
    DomainTask::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(entity: DomainTask): OneTimeWorkRequest {
            return createWorkRequest<AddTaskWorker, DomainTask, Long>(entity)
        }

        private suspend fun action(
            addTask: TaskAddingUseCase,
            task: DomainTask
        ): kotlin.Result<Long> {
            return addTask(task).map { it.first() }
        }
    }
}
