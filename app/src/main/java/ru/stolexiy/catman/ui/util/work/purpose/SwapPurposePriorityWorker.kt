package ru.stolexiy.catman.ui.util.work.purpose

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeUpdatingRepository
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class SwapPurposePriorityWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val gettingRepository: PurposeGettingRepository,
    private val updatingRepository: PurposeUpdatingRepository,
) : AbstractWorker<SwapPurposePriorityWorker.PurposePair, Unit>(
    WorkUtils.UPDATING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.purpose_updating),
    "Purpose swapping priority",
    { action(gettingRepository, updatingRepository, it) },
    PurposePair::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(firstId: Long, secondId: Long): OneTimeWorkRequest {
            return createWorkRequest<SwapPurposePriorityWorker, PurposePair, Unit>(
                PurposePair(firstId, secondId)
            )
        }

        private suspend fun action(
            purposeGet: PurposeGettingRepository,
            purposeUpdate: PurposeUpdatingRepository,
            purposePair: PurposePair
        ): kotlin.Result<Unit> = runCatching {
            val first = purposeGet.byId(purposePair.firstId).first().getOrThrow()
                ?: throw IllegalArgumentException()
            val second = purposeGet.byId(purposePair.secondId).first().getOrThrow()
                ?: throw IllegalArgumentException()

            purposeUpdate(
                first.copy(priority = second.priority),
                second.copy(priority = first.priority)
            ).getOrThrow()
        }
    }

    data class PurposePair(
        val firstId: Long,
        val secondId: Long
    )
}
