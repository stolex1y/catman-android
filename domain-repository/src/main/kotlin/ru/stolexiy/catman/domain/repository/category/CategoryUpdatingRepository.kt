package ru.stolexiy.catman.domain.repository.category

import ru.stolexiy.catman.domain.model.DomainCategory

interface CategoryUpdatingRepository {
    suspend operator fun invoke(vararg categories: DomainCategory): Result<Unit>
}
