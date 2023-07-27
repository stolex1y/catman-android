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
import ru.stolexiy.catman.domain.repository.task.TaskDeletingRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingRepository
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class DeleteTaskWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val gettingRepository: TaskGettingRepository,
    private val deletingRepository: TaskDeletingRepository,
) : AbstractWorker<Long, DomainTask>(
    WorkUtils.DELETING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.task_deleting),
    "Task deleting",
    { action(gettingRepository, deletingRepository, it) },
    Long::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(id: Long): OneTimeWorkRequest {
            return createWorkRequest<DeleteTaskWorker, Long, DomainTask>(id)
        }

        private suspend fun action(
            taskGet: TaskGettingRepository,
            taskDelete: TaskDeletingRepository,
            id: Long
        ): kotlin.Result<DomainTask> {
            return runCatching {
                val deletedPurpose = taskGet.byId(id).first().getOrThrow()
                    ?: throw IllegalArgumentException()
                taskDelete.byId(id)
                return@runCatching deletedPurpose
            }
        }
    }
}
