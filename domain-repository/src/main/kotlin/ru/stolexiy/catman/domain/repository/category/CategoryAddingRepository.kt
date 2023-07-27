package ru.stolexiy.catman.domain.repository.category

import ru.stolexiy.catman.domain.model.DomainCategory

interface CategoryAddingRepository {
    suspend operator fun invoke(vararg categories: DomainCategory): Result<List<Long>>
}
