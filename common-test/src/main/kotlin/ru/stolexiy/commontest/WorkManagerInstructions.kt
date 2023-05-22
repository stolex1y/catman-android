package ru.stolexiy.commontest

import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.azimolabs.conditionwatcher.Instruction


fun waitAllWorkersInstruction(workManager: WorkManager): Instruction {
    return object : Instruction() {
        private val workInfosProvider = {
            workManager.getWorkInfos(
                WorkQuery.fromStates(
                    WorkInfo.State.values().filter { !it.isFinished })
            ).get()
        }

        override fun getDescription(): String {
            return "Wait until all WorkManager workers finished"
        }

        override fun checkCondition(): Boolean {
            return workInfosProvider().isEmpty()
        }
    }
}
