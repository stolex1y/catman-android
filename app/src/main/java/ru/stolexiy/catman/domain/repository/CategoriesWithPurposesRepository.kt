package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose

interface CategoriesWithPurposesRepository {
    fun getAllCategoriesWithPurposes(): Flow<Map<DomainCategory, List<DomainPurpose>>>
}