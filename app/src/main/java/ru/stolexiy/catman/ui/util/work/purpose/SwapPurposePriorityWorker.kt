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
import ru.stolexiy.catman.domain.usecase.purpose.PurposeGettingUseCase
import ru.stolexiy.catman.domain.usecase.purpose.PurposeUpdatingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class SwapPurposePriorityWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getPurpose: PurposeGettingUseCase,
    private val updatePurpose: PurposeUpdatingUseCase,
) : AbstractWorker<SwapPurposePriorityWorker.PurposePair, Unit>(
    PurposePair::class,
    appContext,
    workerParams
) {

    override val workName: String = CHANGE_PURPOSE_PRIORITY
    override val notificationMsg: String = applicationContext.getString(R.string.purpose_updating)
    override val notificationId: Int = WorkUtils.UPDATE_PURPOSE_NOTIFICATION_ID

    companion object {
        const val CHANGE_PURPOSE_PRIORITY = "Swap purposes priority"

        fun createWorkRequest(firstId: Long, secondId: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SwapPurposePriorityWorker>()
                .addTag(CHANGE_PURPOSE_PRIORITY)
                .setInputData(WorkUtils.serialize(PurposePair(firstId, secondId)))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun calculate(inputArg: PurposePair): kotlin.Result<Unit> {
        val first = getPurpose.byId(inputArg.firstId).first().getOrThrow()
        val second = getPurpose.byId(inputArg.secondId).first().getOrThrow()
        if (first == null || second == null)
            return kotlin.Result.success(Unit)
        return updatePurpose(
            first.copy(priority = second.priority),
            second.copy(priority = first.priority)
        )
    }

    data class PurposePair(
        val firstId: Long,
        val secondId: Long
    )
}
