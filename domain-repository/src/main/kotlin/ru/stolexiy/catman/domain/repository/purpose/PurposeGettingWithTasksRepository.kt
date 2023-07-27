package ru.stolexiy.catman.domain.repository.purpose

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.model.DomainTask

interface PurposeGettingWithTasksRepository {
    fun all(
//        purposeSort: Sort<DomainPurpose.Fields> = Sort.asc(DomainPurpose.Fields.ID),
//        taskSort: Sort<DomainTask.Fields> = Sort.asc(DomainTask.Fields.ID)
    ): Flow<Result<Map<DomainPurpose, List<DomainTask>>>>

    suspend fun allOnce(
//        purposeSort: Sort<DomainPurpose.Fields> = Sort.asc(DomainPurpose.Fields.ID),
//        taskSort: Sort<DomainTask.Fields> = Sort.asc(DomainTask.Fields.ID)
    ): Result<Map<DomainPurpose, List<DomainTask>>>

    fun byId(purposeId: Long): Flow<Result<Pair<DomainPurpose, List<DomainTask>>?>>
    suspend fun byIdOnce(purposeId: Long): Result<Pair<DomainPurpose, List<DomainTask>>?>
}
