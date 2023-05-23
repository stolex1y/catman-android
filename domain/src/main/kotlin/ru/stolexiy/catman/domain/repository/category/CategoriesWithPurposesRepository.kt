package ru.stolexiy.catman.domain.repository.category

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose

interface CategoriesWithPurposesRepository {
    fun getAll(): Flow<Map<DomainCategory, List<DomainPurpose>>>
}
