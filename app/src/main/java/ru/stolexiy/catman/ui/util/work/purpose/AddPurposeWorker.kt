package ru.stolexiy.catman.ui.util.work.purpose

import android.app.Notification
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.PurposeCrud
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
        val INPUT_PURPOSE = "INPUT_PURPOSE"
    }

    override suspend fun doWork(): Result {
        val addingPurpose = inputData.keyValueMap[INPUT_PURPOSE] as DomainPurpose? ?: return Result.success()
        return kotlin.runCatching {
            Timber.d("adding purpose started")
            purposeCrud.create(addingPurpose)
        }.onFailure {
            Timber.e(it, "adding purpose cancelled with error")
            if (it is CancellationException) throw it
        }.onSuccess {
            Timber.d("purpose added successfully")
        }.let {
            if (it.isSuccess)
                Result.success()
            else
                Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo()
    }

    private fun createNotification(): Notification {
        TODO("notification about background working")
//        Notification.Builder(applicationContext, NotificationChannels.BACKGROUND)
    }
}