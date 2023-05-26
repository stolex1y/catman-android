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
import ru.stolexiy.catman.domain.usecase.purpose.PurposeAddingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils
import ru.stolexiy.catman.ui.util.work.WorkUtils.ADD_PURPOSE_NOTIFICATION_ID

@HiltWorker
class AddPurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val addPurpose: PurposeAddingUseCase,
) : AbstractWorker<DomainPurpose, Long>(DomainPurpose::class, appContext, workerParams) {

    override val workName: String = ADD_PURPOSE_TAG
    override val notificationMsg: String = applicationContext.getString(R.string.purpose_adding)
    override val notificationId: Int = ADD_PURPOSE_NOTIFICATION_ID

    companion object {
        const val ADD_PURPOSE_TAG = "Purpose adding"

        fun createWorkRequest(addingPurpose: DomainPurpose): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<AddPurposeWorker>()
                .addTag(ADD_PURPOSE_TAG)
                .setInputData(WorkUtils.serialize(addingPurpose))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun calculate(inputArg: DomainPurpose): kotlin.Result<Long> {
        return addPurpose(inputArg).map { it.first() }
    }
}
