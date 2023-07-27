package ru.stolexiy.catman.domain.usecase.task

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.common.DateUtils.isNotPast

object TaskConstraints {
    suspend fun DomainTask.checkPurposeIsExist(
        purposeGet: PurposeGettingRepository
    ): DomainPurpose {
        return purposeGet.byIdOnce(purposeId).getOrThrow()
            ?: throw IllegalArgumentException("Parent purpose must be exist")
    }

    fun DomainTask.checkDeadlineIsNotPast() {
        require(deadline.isNotPast()) { "The deadline can't be past" }
    }

    fun DomainTask.checkStartTimeIsNotPast() {
        startTime?.let {
            require(it.isBefore(deadline)) { "The start time can't be after deadline" }
        }
    }
}
