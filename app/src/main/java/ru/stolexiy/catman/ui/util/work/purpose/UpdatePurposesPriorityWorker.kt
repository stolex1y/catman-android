package ru.stolexiy.catman.ui.util.work.purpose

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.usecase.purpose.PurposeUpdatingPriorityUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class UpdatePurposesPriorityWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val updatingPriorityUseCase: PurposeUpdatingPriorityUseCase,
) : AbstractWorker<UpdatePurposesPriorityWorker.PriorityById, Unit>(
    WorkUtils.UPDATING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.purpose_updating),
    "Update purposes priority",
    { action(updatingPriorityUseCase, it) },
    PriorityById::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(prioritiesById: Map<Long, Int>): OneTimeWorkRequest {
            return createWorkRequest<UpdatePurposesPriorityWorker, PriorityById, Unit>(
                PriorityById(prioritiesById)
            )
        }

        private suspend fun action(
            updatePriorities: PurposeUpdatingPriorityUseCase,
            priorityById: PriorityById
        ): kotlin.Result<Unit> = updatePriorities(priorityById.map)
    }

    data class PriorityById(
        val map: Map<Long, Int>
    )
}
