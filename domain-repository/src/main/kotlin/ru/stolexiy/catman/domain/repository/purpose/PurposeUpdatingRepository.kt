package ru.stolexiy.catman.domain.repository.purpose

import ru.stolexiy.catman.domain.model.DomainPurpose

interface PurposeUpdatingRepository {
    suspend operator fun invoke(vararg purposes: DomainPurpose): Result<Unit>
}
