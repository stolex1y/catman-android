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
import kotlinx.coroutines.flow.first
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.Json
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.util.work.WorkUtils
import timber.log.Timber

@HiltWorker
class UpdatePurposeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val purposeCrud: PurposeCrud
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        val UPDATE_PURPOSE_TAG = UpdatePurposeWorker::class.simpleName ?: "UpdatePurposeWorker"
        const val INPUT_PURPOSE = "INPUT_PURPOSE"
        const val OUTPUT_BEFORE_CHANGE = "OUTPUT_PURPOSE_BEFORE_CHANGE"

        fun create(updatingPurpose: DomainPurpose): OneTimeWorkRequest {
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

    override suspend fun doWork(): Result {
        val updatingPurposeString = inputData.getString(INPUT_PURPOSE) ?: return Result.success()
        val updatingPurpose =
            Json.serializer.fromJson(updatingPurposeString, DomainPurpose::class.java)
        var beforeChanging: DomainPurpose?
        return run {
            Timber.d("updating purpose with id ${updatingPurpose.id} started")
            beforeChanging = purposeCrud.get(updatingPurpose.id).first().getOrNull()
            if (beforeChanging == null)
                return@run kotlin.Result.failure(IllegalArgumentException())
            purposeCrud.update(updatingPurpose)
        }.onFailure {
            Timber.e(it, "updating purpose with id ${updatingPurpose.id} cancelled with error")
        }.onSuccess {
            Timber.d("purpose updated with id ${updatingPurpose.id} successfully")
        }.let {
            if (it.isSuccess) {
                val updatingBeforeChange =
                    Json.serializer.toJson(beforeChanging, DomainPurpose::class.java)
                Result.success(workDataOf(OUTPUT_BEFORE_CHANGE to updatingBeforeChange))
            } else
                Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(WorkUtils.UPDATE_PURPOSE_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return WorkUtils.createNotificationBackgroundWork(
            applicationContext.getString(R.string.purpose_updating),
            applicationContext
        )
    }
}
