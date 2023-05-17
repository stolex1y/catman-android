package ru.stolexiy.catman.ui.util.work.purpose

import android.app.Notification
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.util.work.WorkUtils
import timber.log.Timber

@HiltWorker
class DeletePurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val purposeCrud: PurposeCrud
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        val DELETE_PURPOSE_TAG = DeletePurposeWorker::class.simpleName ?: "DeletePurposeWorker"
        const val INPUT_PURPOSE_ID = "INPUT_PURPOSE_ID"

        fun createWorkRequest(deletingPurposeId: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DeletePurposeWorker>()
                .addTag(DELETE_PURPOSE_TAG)
                .setInputData(workDataOf(INPUT_PURPOSE_ID to deletingPurposeId))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun doWork(): Result {
        val deletingPurposeId: Long =
            inputData.keyValueMap[INPUT_PURPOSE_ID] as Long? ?: return Result.success()
        return run {
            Timber.d("deleting purpose with id $deletingPurposeId started")
            purposeCrud.delete(deletingPurposeId)
        }.onFailure {
            Timber.e(it, "deleting purpose with id $deletingPurposeId cancelled with error")
        }.onSuccess {
            Timber.d("purpose with id $deletingPurposeId deleted successfully")
        }.let {
            if (it.isSuccess)
                Result.success()
            else
                Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(WorkUtils.DELETE_PURPOSE_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return WorkUtils.createNotificationBackgroundWork(
            applicationContext.getString(R.string.purpose_deleting),
            applicationContext
        )
    }
}
