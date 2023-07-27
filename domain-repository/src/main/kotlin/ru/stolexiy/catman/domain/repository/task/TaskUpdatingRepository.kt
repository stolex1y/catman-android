package ru.stolexiy.catman.domain.repository.task

import ru.stolexiy.catman.domain.model.DomainTask

interface TaskUpdatingRepository {
    suspend operator fun invoke(vararg tasks: DomainTask): Result<Unit>
}
