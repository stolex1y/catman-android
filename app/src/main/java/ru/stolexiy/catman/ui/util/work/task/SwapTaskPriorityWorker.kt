package ru.stolexiy.catman.ui.util.work.task

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.repository.task.TaskGettingRepository
import ru.stolexiy.catman.domain.repository.task.TaskUpdatingRepository
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class SwapTaskPriorityWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val gettingRepository: TaskGettingRepository,
    private val updatingRepository: TaskUpdatingRepository,
) : AbstractWorker<SwapTaskPriorityWorker.TaskPair, Unit>(
    WorkUtils.UPDATING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.task_updating),
    "Task swapping priority",
    { action(gettingRepository, updatingRepository, it) },
    TaskPair::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(firstId: Long, secondId: Long): OneTimeWorkRequest {
            return createWorkRequest<SwapTaskPriorityWorker, TaskPair, Unit>(
                TaskPair(firstId, secondId)
            )
        }

        private suspend fun action(
            getPurpose: TaskGettingRepository,
            updatePurpose: TaskUpdatingRepository,
            purposePair: TaskPair
        ): kotlin.Result<Unit> = runCatching {
            val first = getPurpose.byId(purposePair.firstId).first().getOrThrow()
                ?: throw IllegalArgumentException()
            val second = getPurpose.byId(purposePair.secondId).first().getOrThrow()
                ?: throw IllegalArgumentException()

            updatePurpose(
                first.copy(priority = second.priority),
                second.copy(priority = first.priority)
            ).getOrThrow()
        }
    }

    data class TaskPair(
        val firstId: Long,
        val secondId: Long
    )
}
