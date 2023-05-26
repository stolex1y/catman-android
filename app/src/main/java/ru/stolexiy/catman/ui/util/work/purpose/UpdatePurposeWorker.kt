package ru.stolexiy.catman.ui.util.work.purpose

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.purpose.PurposeUpdatingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class UpdatePurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val updatePurpose: PurposeUpdatingUseCase
) : AbstractWorker<DomainPurpose, Unit>(DomainPurpose::class, appContext, workerParams) {

    override val notificationId: Int = WorkUtils.UPDATE_PURPOSE_NOTIFICATION_ID
    override val notificationMsg: String = applicationContext.getString(R.string.purpose_updating)
    override val workName: String = UPDATE_PURPOSE_TAG

    companion object {
        const val UPDATE_PURPOSE_TAG = "Purpose updating"

        fun createWorkRequest(updatingPurpose: DomainPurpose): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<UpdatePurposeWorker>()
                .setInputData(WorkUtils.serialize(updatingPurpose))
                .addTag(UPDATE_PURPOSE_TAG)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun calculate(inputArg: DomainPurpose): kotlin.Result<Unit> {
        return updatePurpose(inputArg)
    }
}
