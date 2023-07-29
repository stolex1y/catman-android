package ru.stolexiy.catman.ui.util.work.purpose

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeDeletingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class DeletePurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val purposeGet: PurposeGettingRepository,
    private val purposeDelete: PurposeDeletingRepository,
) : AbstractWorker<Long, DomainPurpose?>(
    WorkUtils.DELETING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.purpose_deleting),
    "Purpose deleting",
    { action(purposeGet, purposeDelete, it) },
    Long::class,
    appContext,
    workerParams
) {
    companion object {
        private const val DELETE_PURPOSE_TAG = "Purpose deleting"

        fun createWorkRequest(deletingPurposeId: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DeletePurposeWorker>()
                .addTag(DELETE_PURPOSE_TAG)
                .setInputData(WorkUtils.serialize(deletingPurposeId))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }

        private suspend fun action(
            purposeGet: PurposeGettingRepository,
            purposeDelete: PurposeDeletingRepository,
            purposeId: Long
        ): kotlin.Result<DomainPurpose?> {
            return runCatching {
                val deletingPurpose = CoroutineScope(currentCoroutineContext()).async {
                    purposeGet.byId(purposeId).first().getOrThrow()
                }
                purposeDelete.byId(purposeId)
                return@runCatching deletingPurpose.await()
            }
        }
    }
}
