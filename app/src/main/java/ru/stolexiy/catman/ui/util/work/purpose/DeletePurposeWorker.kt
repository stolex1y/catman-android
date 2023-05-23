package ru.stolexiy.catman.ui.util.work.purpose

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.usecase.purpose.PurposeDeletingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class DeletePurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val deletePurpose: PurposeDeletingUseCase
) : AbstractWorker<Long, Unit>(appContext, workerParams) {

    override val notificationId: Int = WorkUtils.DELETE_PURPOSE_NOTIFICATION_ID
    override val notificationMsg: String = applicationContext.getString(R.string.purpose_deleting)
    override val workName: String = DELETE_PURPOSE_TAG

    companion object {
        private const val DELETE_PURPOSE_TAG = "Purpose deleting"
        private const val INPUT_PURPOSE_ID = "INPUT_PURPOSE_ID"

        fun createWorkRequest(deletingPurposeId: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DeletePurposeWorker>()
                .addTag(DELETE_PURPOSE_TAG)
                .setInputData(deletingPurposeId.serialize())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }

        private fun Long.serialize(): Data {
            return workDataOf(INPUT_PURPOSE_ID to this)
        }
    }

    override fun Data.deserialize(): Long {
        return this.keyValueMap[INPUT_PURPOSE_ID] as Long
    }

    override suspend fun calculate(inputArg: Long): kotlin.Result<Unit> {
        return deletePurpose.byId(inputArg)
    }
}
