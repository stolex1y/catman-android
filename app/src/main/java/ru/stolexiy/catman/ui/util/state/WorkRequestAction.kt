package ru.stolexiy.catman.ui.util.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest

/**
 * Implements abstract Action with WorkRequest (see the {@link androidx.work.WorkManager} class).
 * This implementation also is cancelable (see {@link cancel} method). You can observe the action
 * status with eponymous property (see {@link Action} class).
 *
 * @param mRevertingWorkProvider - work request provider to revert action with mWorkRequest param.
 */
class WorkRequestAction(
    private val mWorkManager: WorkManager,
    private val mWorkRequest: WorkRequest,
    private val mRevertingWorkProvider: ((output: Data) -> WorkRequest?)? = null
) : Action() {

    override val canRevert = mRevertingWorkProvider != null
    override val canCancel = true

    private var mLiveWorkInfo: LiveData<WorkInfo>? = null
    private var mRevertingLiveWorkInfo: LiveData<WorkInfo>? = null

    private val workInfoStatusObserver by lazy {
        object : Observer<WorkInfo> {
            override fun onChanged(value: WorkInfo) {
                mStatus.value = when (value.state) {
                    WorkInfo.State.SUCCEEDED -> Status.Success
                    WorkInfo.State.CANCELLED -> Status.Canceled
                    WorkInfo.State.FAILED -> Status.Error
                    else -> Status.Running
                }
                if (value.state.isFinished)
                    this@WorkRequestAction.mRevertingLiveWorkInfo?.removeObserver(this)
            }
        }
    }

    private val revertingWorkInfoStatusObserver by lazy {
        object : Observer<WorkInfo> {
            override fun onChanged(value: WorkInfo) {
                mStatus.value = when (value.state) {
                    WorkInfo.State.SUCCEEDED -> Status.Reverted
                    WorkInfo.State.CANCELLED -> Status.Canceled
                    WorkInfo.State.FAILED -> Status.Error
                    else -> Status.Running
                }
                if (value.state.isFinished)
                    this@WorkRequestAction.mRevertingLiveWorkInfo?.removeObserver(this)
            }
        }
    }

    override fun cancel(): Boolean {
        return when (mLiveWorkInfo?.value?.state) {
            WorkInfo.State.RUNNING -> {
                mWorkManager.cancelWorkById(mWorkRequest.id)
                mStatus.value = Status.Canceled
                true
            }
            null -> throw IllegalStateException("Action isn't started")
            else -> false
        }
    }

    override fun cancelReverting(): Boolean {
        return when (mRevertingLiveWorkInfo?.value?.state) {
            WorkInfo.State.RUNNING -> {
                mWorkManager.cancelWorkById(mWorkRequest.id)
                true
            }
            null -> throw IllegalStateException("Action isn't started")
            else -> false
        }
    }

    override fun revert(): Boolean {
        if (!canRevert)
            throw NotImplementedError()

        if (mRevertingLiveWorkInfo != null)
            return true

        val workInfo = mLiveWorkInfo?.value

        if (workInfo == null || workInfo.state != WorkInfo.State.SUCCEEDED)
            return false

        val revertingWork = getRevertingWork(workInfo.outputData) ?: return false
        mWorkManager.enqueue(revertingWork)
        mRevertingLiveWorkInfo = mWorkManager.getWorkInfoByIdLiveData(revertingWork.id)
        return true
    }

    override fun start() {
        if (mLiveWorkInfo != null)
            return

        mWorkManager.enqueue(mWorkRequest)

        mLiveWorkInfo = mWorkManager.getWorkInfoByIdLiveData(mWorkRequest.id).apply {
            observeForever(workInfoStatusObserver)
        }
    }

    private fun getRevertingWork(output: Data): WorkRequest? {
        return mRevertingWorkProvider?.invoke(output)
    }
}
