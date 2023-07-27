package ru.stolexiy.catman.domain.usecase.task

import kotlinx.coroutines.CoroutineDispatcher
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.repository.task.TaskAddingRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingByPurposeRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class TaskUpdatingUseCase @Inject constructor(
    private val taskAdd: TaskAddingRepository,
    private val purposeGet: PurposeGettingRepository,
    private val taskGetByPurpose: TaskGettingByPurposeRepository,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {

}