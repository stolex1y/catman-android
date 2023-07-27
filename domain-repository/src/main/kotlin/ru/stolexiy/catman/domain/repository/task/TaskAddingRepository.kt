package ru.stolexiy.catman.domain.repository.task

import ru.stolexiy.catman.domain.model.DomainTask

interface TaskAddingRepository {
    suspend operator fun invoke(vararg tasks: DomainTask): Result<List<Long>>
}
