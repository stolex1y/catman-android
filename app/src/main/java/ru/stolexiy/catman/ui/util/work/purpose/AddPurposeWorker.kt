package ru.stolexiy.catman.ui.util.work.purpose

import android.app.Notification
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.Json
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.util.work.WorkUtils
import timber.log.Timber

class AddPurposeWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val purposeCrud: PurposeCrud

    init {
        (applicationContext as CatmanApplication).let {
            purposeCrud = it.purposeCrud
        }
    }

    companion object {
        val ADD_PURPOSE_TAG = AddPurposeWorker::class.simpleName ?: "AddPurposeWorker"
        const val INPUT_PURPOSE = "INPUT_PURPOSE"
        const val OUTPUT_PURPOSE_ID = "OUTPUT_PURPOSE_ID"

        fun create(addingPurpose: DomainPurpose): OneTimeWorkRequest {
            val workerInput = Json.serializer.toJson(addingPurpose).let {
                workDataOf(INPUT_PURPOSE to it)
            }
            return OneTimeWorkRequestBuilder<AddPurposeWorker>()
                .addTag(ADD_PURPOSE_TAG)
                .setInputData(workerInput)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun doWork(): Result {
        val addingPurposeString = inputData.getString(INPUT_PURPOSE) ?: return Result.success()
        val addingPurpose = Json.serializer.fromJson(addingPurposeString, DomainPurpose::class.java)
        return run {
            Timber.d("adding purpose started")
            purposeCrud.create(addingPurpose)
        }.onFailure {
            Timber.e(it, "adding purpose cancelled with error")
        }.onSuccess {
            Timber.d("purpose added successfully")
        }.let {
            if (it.isSuccess)
                Result.success(workDataOf(OUTPUT_PURPOSE_ID to it.getOrNull()!!.first()))
            else
                Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(WorkUtils.ADD_PURPOSE_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return WorkUtils.createNotificationBackgroundWork(
            applicationContext.getString(R.string.purpose_adding),
            applicationContext
        )
    }
}