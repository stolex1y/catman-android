package ru.stolexiy.catman.ui.dialog.purpose.add

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.ui.util.udf.Action
import ru.stolexiy.catman.ui.util.udf.SimpleLoadingState
import ru.stolexiy.catman.ui.util.udf.WorkAction
import ru.stolexiy.catman.ui.util.work.purpose.AddPurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker

sealed interface AddPurposeAction : Action<SimpleLoadingState> {
    class Do(
        purpose: DomainPurpose,
        workManager: WorkManager
    ) : AddPurposeAction, WorkAction(workManager) {
        override val workRequest = AddPurposeWorker.create(purpose)
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
                DeletePurposeWorker.create(deletingId)
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
