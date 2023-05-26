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
import ru.stolexiy.catman.domain.usecase.purpose.PurposeDeletingUseCase
import ru.stolexiy.catman.domain.usecase.purpose.PurposeGettingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class DeletePurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getPurpose: PurposeGettingUseCase,
    private val deletePurpose: PurposeDeletingUseCase
) : AbstractWorker<Long, DomainPurpose?>(Long::class, appContext, workerParams) {

    override val notificationId: Int = WorkUtils.DELETE_PURPOSE_NOTIFICATION_ID
    override val notificationMsg: String = applicationContext.getString(R.string.purpose_deleting)
    override val workName: String = DELETE_PURPOSE_TAG

    companion object {
        private const val DELETE_PURPOSE_TAG = "Purpose deleting"

        fun createWorkRequest(deletingPurposeId: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DeletePurposeWorker>()
                .addTag(DELETE_PURPOSE_TAG)
                .setInputData(WorkUtils.serialize(deletingPurposeId))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun calculate(inputArg: Long): kotlin.Result<DomainPurpose?> {
        return runCatching {
            val deletingPurpose = CoroutineScope(currentCoroutineContext()).async {
                getPurpose.byId(inputArg).first().getOrThrow()
            }
            deletePurpose.byId(inputArg)
            return@runCatching deletingPurpose.await()
        }
    }
}
