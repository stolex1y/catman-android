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
import ru.stolexiy.catman.ui.util.work.WorkUtils.ADD_PURPOSE_NOTIFICATION_ID

@HiltWorker
class AddPurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val purposeCrud: PurposeCrud
) : AbstractWorker<DomainPurpose, Long>(appContext, workerParams) {

    override val workName: String = ADD_PURPOSE_TAG
    override val notificationMsg: String = applicationContext.getString(R.string.purpose_adding)
    override val notificationId: Int = ADD_PURPOSE_NOTIFICATION_ID

    companion object {
        private const val INPUT_PURPOSE = "INPUT_PURPOSE"
        const val ADD_PURPOSE_TAG = "Purpose adding"

        fun createWorkRequest(addingPurpose: DomainPurpose): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<AddPurposeWorker>()
                .addTag(ADD_PURPOSE_TAG)
                .setInputData(addingPurpose.serialize())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }

        private fun DomainPurpose.serialize(): Data =
            Json.serializer.toJson(this).let {
                workDataOf(INPUT_PURPOSE to it)
            }
    }

    override fun Data.deserialize(): DomainPurpose {
        val addingPurposeString = inputData.getString(INPUT_PURPOSE)!!
        return Json.serializer.fromJson(addingPurposeString, DomainPurpose::class.java)
    }

    override suspend fun calculate(inputArg: DomainPurpose): kotlin.Result<Long> {
        return purposeCrud.create(inputArg).map { it.first() }
    }
}
