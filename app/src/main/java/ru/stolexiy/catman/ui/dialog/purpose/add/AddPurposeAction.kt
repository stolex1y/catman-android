package ru.stolexiy.catman.ui.dialog.purpose.add

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.ui.util.state.WorkRequestAction
import ru.stolexiy.catman.ui.util.udf.Action
import ru.stolexiy.catman.ui.util.udf.SimpleLoadingState
import ru.stolexiy.catman.ui.util.udf.WorkAction
import ru.stolexiy.catman.ui.util.work.purpose.AddPurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker

sealed interface AddPurposeAction : Action<SimpleLoadingState> {
    class Do constructor(
        purpose: DomainPurpose,
        workManager: WorkManager,
        appContext: Context
    ) : AddPurposeAction {
        private val workRequestAction = WorkRequestAction(
            workManager,
            AddPurposeWorker.create(purpose)
        ) {
            val addedPurposeId = AddPurposeWorker.parseOutputPurposeId(it)
            if (addedPurposeId == null)
                null
            else
                DeletePurposeWorker.createWorkRequest(addedPurposeId)
        }

        override suspend fun invoke(): Flow<SimpleLoadingState> {
            return flow {
                workRequestAction.status.collect {
                    emit(it)
                }
            }
        }

        override suspend fun cancel() {
//            workRequestAction.
        }
    }

    class Revert(
        addPurposeAction: Do,
        workManager: WorkManager
    ) : AddPurposeAction, WorkAction(workManager) {
        override val workRequest: OneTimeWorkRequest?

        init {
            workRequest = addPurposeAction.workRequestOutput?.let {
                val deletingId =
                    it.keyValueMap[AddPurposeWorker.OUTPUT_PURPOSE_ID] as Long
                DeletePurposeWorker.createWorkRequest(deletingId)
            }
        }
    }

    class LoadInitialState() : AddPurposeAction {
        override suspend fun invoke(): Flow<SimpleLoadingState> {
            TODO("Not yet implemented")
        }

        override fun cancel() {
            TODO("Not yet implemented")
        }

    }
}
