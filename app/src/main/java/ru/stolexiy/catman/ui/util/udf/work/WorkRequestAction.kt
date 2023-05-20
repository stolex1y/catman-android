/*
package ru.stolexiy.catman.ui.util.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import ru.stolexiy.catman.ui.util.udf.Action

*/
/**
 * Implements abstract Action with WorkRequest (see the {@link androidx.work.WorkManager} class).
 * This implementation also is cancelable (see {@link cancel} method). You can observe the action
 * status with eponymous property (see {@link Action} class).
 *//*

class WorkRequestAction(
    private val workManager: WorkManager,
    private val workRequest: WorkRequest
) : Action() {

    override val canCancel = true

    private var liveWorkInfo: LiveData<WorkInfo>? = null

    private val workInfoStatusObserver by lazy {
        object : Observer<WorkInfo> {
            override fun onChanged(value: WorkInfo) {
                _state.value = when (value.state) {
                    WorkInfo.State.SUCCEEDED -> Status.Success
                    WorkInfo.State.CANCELLED -> Status.Canceled
                    WorkInfo.State.FAILED -> Status.Error
                    else -> Status.Running
                }
                if (value.state.isFinished)
                    liveWorkInfo?.removeObserver(this)
            }
        }
    }

    override fun cancel(): Boolean {
        return if (state.value == Status.Running) {
            workManager.cancelWorkById(workRequest.id)
            true
        } else {
            false
        }
    }

    override suspend operator fun invoke() {
        if (state.value != Status.Created)
            return

        workManager.enqueue(workRequest)

        liveWorkInfo = workManager.getWorkInfoByIdLiveData(workRequest.id).apply {
            observeForever(workInfoStatusObserver)
        }
    }
}
*/
