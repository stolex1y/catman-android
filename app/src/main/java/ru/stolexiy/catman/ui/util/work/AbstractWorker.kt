package ru.stolexiy.catman.ui.util.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.ui.util.work.WorkUtils.deserialize
import timber.log.Timber
import kotlin.reflect.KClass

abstract class AbstractWorker<I : Any, O> protected constructor(
    private val inputArgClass: KClass<I>,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    protected abstract val notificationId: Int
    protected abstract val notificationMsg: String
    protected abstract val workName: String

    final override suspend fun doWork(): Result {
        val inputArg: I = inputData.deserialize(inputArgClass)!!
        Timber.d("'$workName' started")
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

//    protected abstract fun Data.deserialize(): I
    protected abstract suspend fun calculate(inputArg: I): kotlin.Result<O>
}
