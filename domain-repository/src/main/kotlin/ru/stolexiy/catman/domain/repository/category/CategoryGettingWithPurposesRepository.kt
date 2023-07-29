package ru.stolexiy.catman.domain.repository.category

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose

interface CategoryGettingWithPurposesRepository {
    fun all(
//        categorySort: Sort<DomainCategory.Fields> = Sort.asc(DomainCategory.Fields.ID),
//        purposeSort: Sort<DomainPurpose.Fields> = Sort.asc(DomainPurpose.Fields.ID)
    ): Flow<Result<Map<DomainCategory, List<DomainPurpose>>>>

    fun allOrderedByPurposePriority(
        asc: Boolean = true
    ): Flow<Result<Map<DomainCategory, List<DomainPurpose>>>>
}
