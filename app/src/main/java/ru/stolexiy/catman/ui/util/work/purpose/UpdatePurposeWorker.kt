package ru.stolexiy.catman.ui.util.work.purpose

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.usecase.purpose.PurposeUpdatingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class UpdatePurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val purposeGet: PurposeGettingRepository,
    private val purposeUpdate: PurposeUpdatingUseCase
) : AbstractWorker<DomainPurpose, DomainPurpose>(
    WorkUtils.UPDATING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.purpose_updating),
    "Purpose updating",
    { action(purposeGet, purposeUpdate, it) },
    DomainPurpose::class,
    appContext,
    workerParams
) {
    companion object {
        const val UPDATE_PURPOSE_TAG = "Purpose updating"

        fun createWorkRequest(updatingPurpose: DomainPurpose): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<UpdatePurposeWorker>()
                .setInputData(WorkUtils.serialize(updatingPurpose))
                .addTag(UPDATE_PURPOSE_TAG)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }

        private suspend fun action(
            purposeGet: PurposeGettingRepository,
            purposeUpdate: PurposeUpdatingUseCase,
            purpose: DomainPurpose
        ): kotlin.Result<DomainPurpose> = runCatching {
            val untilUpdate = purposeGet.byId(purpose.id).first().getOrThrow()
                ?: throw IllegalArgumentException()
            purposeUpdate(purpose).getOrThrow()
            return@runCatching untilUpdate
        }
    }
}
