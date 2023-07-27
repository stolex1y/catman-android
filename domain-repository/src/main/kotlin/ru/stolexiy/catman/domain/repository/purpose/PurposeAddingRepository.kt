package ru.stolexiy.catman.domain.repository.purpose

import ru.stolexiy.catman.domain.model.DomainPurpose

interface PurposeAddingRepository {
    suspend operator fun invoke(vararg purposes: DomainPurpose): Result<List<Long>>
}
