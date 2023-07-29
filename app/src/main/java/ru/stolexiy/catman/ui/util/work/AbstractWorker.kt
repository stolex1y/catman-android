package ru.stolexiy.catman.ui.util.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import ru.stolexiy.catman.ui.util.work.WorkUtils.deserialize
import timber.log.Timber
import kotlin.reflect.KClass

abstract class AbstractWorker<Input : Any, Output> protected constructor(
    private val notificationId: Int,
    private val notificationMsg: String,
    private val workName: String,
    private val action: suspend (Input) -> kotlin.Result<Output>,
    private val inputArgClass: KClass<Input>,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    final override suspend fun doWork(): Result {
        val inputArg: Input = inputData.deserialize(inputArgClass)!!
        Timber.d("'$workName' started with arg: ${inputData.keyValueMap}")
        var result: Result = Result.failure()
        calculate(inputArg).onFailure {
            Timber.e(it, "'$workName' finished with error")
            result = Result.failure(WorkUtils.serialize(it))
        }.onSuccess {
            Timber.d("'$workName' finished successfully")
            result = if (it is Unit)
                Result.success()
            else
                Result.success(WorkUtils.serialize(it))
        }
        return result
    }

    final override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            notificationId,
            WorkUtils.createNotificationBackgroundWork(
                notificationMsg,
                applicationContext
            )
        )
    }

    private suspend fun calculate(inputArg: Input): kotlin.Result<Output> {
        delay(500)
        return action(inputArg)
    }

    companion object {
        const val TAG = "AbstractWorker"
        inline fun <reified Worker : AbstractWorker<Input, Output>, Input, Output> createWorkRequest(
            input: Input,
            tag: String = TAG
        ): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<Worker>()
                .addTag(tag)
                .setInputData(WorkUtils.serialize(input))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }
}
