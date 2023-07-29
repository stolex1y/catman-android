package ru.stolexiy.catman.ui.util.work.purpose

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.purpose.PurposeAddingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class AddPurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val addPurpose: PurposeAddingUseCase,
) : AbstractWorker<DomainPurpose, Long>(
    WorkUtils.ADDING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.purpose_adding),
    "Purpose adding",
    { action(addPurpose, it) },
    DomainPurpose::class,
    appContext,
    workerParams
) {
    companion object {
        const val ADD_PURPOSE_TAG = "Purpose adding"

        fun createWorkRequest(addingPurpose: DomainPurpose): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<AddPurposeWorker>()
                .addTag(ADD_PURPOSE_TAG)
                .setInputData(WorkUtils.serialize(addingPurpose))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }

        private suspend fun action(
            purposeAdd: PurposeAddingUseCase,
            purpose: DomainPurpose
        ): kotlin.Result<Long> {
            delay(1000)
            return purposeAdd(purpose).map { it.first() }
        }
    }
}
