package ru.stolexiy.catman.ui.util.work.task

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.task.TaskGettingRepository
import ru.stolexiy.catman.domain.repository.task.TaskUpdatingRepository
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class UpdateTaskWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val gettingRepository: TaskGettingRepository,
    private val updatingRepository: TaskUpdatingRepository,
) : AbstractWorker<DomainTask, DomainTask>(
    WorkUtils.UPDATING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.task_updating),
    "Task updating",
    { action(gettingRepository, updatingRepository, it) },
    DomainTask::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(entity: DomainTask): OneTimeWorkRequest {
            return createWorkRequest<UpdateTaskWorker, DomainTask, DomainTask>(entity)
        }

        private suspend fun action(
            taskGet: TaskGettingRepository,
            taskUpdate: TaskUpdatingRepository,
            task: DomainTask
        ): kotlin.Result<DomainTask> = runCatching {
            val untilUpdate = taskGet.byId(task.id).first().getOrThrow()
                ?: throw IllegalArgumentException()
            taskUpdate(task).getOrThrow()
            return@runCatching untilUpdate
        }
    }
}
