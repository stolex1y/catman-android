package ru.stolexiy.catman.ui.util.state

import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker

abstract class Action {
    abstract val canRevert: Boolean
    abstract fun cancel()
    abstract fun restart()
    abstract fun revert()
    abstract fun clear()
}

fun Worker.toAction(wm: WorkManager, revert: ((output: Data) -> WorkRequest)? = null): Action {
    return object : Action(), Observer<WorkInfo> {
        override val canRevert: Boolean = revert != null

        private val mId = this@toAction.id
        private val _mWorkInfo = wm.getWorkInfoByIdLiveData(mId)
        private lateinit var mWorkInfo: WorkInfo

        init {
            wm.getWorkInfoByIdLiveData(mId).observeForever(this)
        }

        override fun onChanged(t: WorkInfo) {
            mWorkInfo = t
        }

        override fun cancel() {
            if (mWorkInfo.state.isFinished)
                return
            wm.cancelWorkById(this@toAction.id)
        }

        override fun restart() {
            wm.cancelWorkById(mId)
            wm.enqueue()
        }

        override fun revert() {
            wm.enqueue(revert())
        }

        override fun

    }
}
