package ru.stolexiy.catman.ui.util.udf

import androidx.lifecycle.asFlow
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import ru.stolexiy.catman.R

abstract class WorkAction(
    protected val workManager: WorkManager
) : Action<S> {
    protected abstract val workRequest: WorkRequest?
    var workRequestOutput: Data? = null

    override suspend fun invoke(): Flow<SimpleLoadingState> {
        return flow {
            workRequest?.let { workRequest ->
                emit(SimpleLoadingState.Loading)
                workManager.enqueue(workRequest)
                workManager.getWorkInfoByIdLiveData(workRequest.id).asFlow().collectLatest {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            workRequestOutput = it.outputData
                            emit(SimpleLoadingState.Loaded)
                        }

                        WorkInfo.State.FAILED -> emit(SimpleLoadingState.Error(R.string.internal_error))
                        WorkInfo.State.CANCELLED -> emit(SimpleLoadingState.Loaded)
                        else -> {}
                    }
                }
                emit(SimpleLoadingState.Loaded)
            } ?: emit(SimpleLoadingState.Loaded)
        }
    }

    override fun cancel() {
        workRequest?.let {
            workManager.cancelWorkById(it.id)
        }
    }
}