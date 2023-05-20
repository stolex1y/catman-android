/*
package ru.stolexiy.catman.ui.util.udf.work

import androidx.lifecycle.asFlow
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.udf.Action
import ru.stolexiy.catman.ui.util.udf.SimpleLoadingState

abstract class WorkAction(
    private val workManager: WorkManager
) : Action<SimpleLoadingState> {
    protected abstract val workRequest: WorkRequest
    protected var invoking: Boolean = false
    private var workRequestOutput: Data? = null

    override suspend fun invoke(): Flow<SimpleLoadingState> {
        return flow {
            workRequest.let { workRequest ->
                invoking = true
                emit(SimpleLoadingState.Loading)
                workManager.enqueue(workRequest)
                workManager.getWorkInfoByIdLiveData(workRequest.id).asFlow().collectLatest {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            emit(SimpleLoadingState.Complete(getResultFromWorkOutput(it.outputData)))
                        }
                        WorkInfo.State.FAILED -> {
                            emit(SimpleLoadingState.Error())
                        }
                        WorkInfo.State.CANCELLED -> emit(SimpleLoadingState.Loaded)
                        else -> {}
                    }
                }
                emit(SimpleLoadingState.Loaded)
            }
        }
    }

    override fun cancel() {
        workRequest.let {
            workManager.cancelWorkById(it.id)
        }
    }

    protected abstract fun getResultFromWorkOutput(outputData: Data)
}
*/
