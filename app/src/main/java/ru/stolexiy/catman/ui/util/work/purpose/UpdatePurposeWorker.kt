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
import ru.stolexiy.catman.core.Json
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class UpdatePurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val purposeCrud: PurposeCrud
) : AbstractWorker<DomainPurpose, Unit>(appContext, workerParams) {

    override val notificationId: Int = WorkUtils.UPDATE_PURPOSE_NOTIFICATION_ID
    override val notificationMsg: String = applicationContext.getString(R.string.purpose_updating)
    override val workName: String = UPDATE_PURPOSE_TAG

    companion object {
        const val UPDATE_PURPOSE_TAG = "Purpose updating"
        private const val INPUT_PURPOSE = "INPUT_PURPOSE"

        fun createWorkRequest(updatingPurpose: DomainPurpose): OneTimeWorkRequest {
            val workerInput = Json.serializer.toJson(updatingPurpose).let {
                workDataOf(INPUT_PURPOSE to it)
            }
            return OneTimeWorkRequestBuilder<UpdatePurposeWorker>()
                .setInputData(workerInput)
                .addTag(UPDATE_PURPOSE_TAG)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override fun Data.deserialize(): DomainPurpose {
        val updatingPurposeString = inputData.getString(INPUT_PURPOSE)!!
        return Json.serializer.fromJson(updatingPurposeString, DomainPurpose::class.java)
    }

    override suspend fun calculate(inputArg: DomainPurpose): kotlin.Result<Unit> {
        return purposeCrud.update(inputArg)
    }
}
