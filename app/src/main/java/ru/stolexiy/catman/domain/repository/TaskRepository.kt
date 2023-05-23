package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainTask

interface TaskRepository {
    fun getAll(): Flow<List<DomainTask>>
    fun get(id: Long): Flow<DomainTask?>


//    TODO: query with PageRequest
    /*fun getAllByPurpose(
        purposeId: Long,
        pageRequest: PageRequest<DomainTask>
    ): Flow<PageResponse<DomainTask>>*/

    fun getAllByPurpose(purposeId: Long): Flow<List<DomainTask>>

//    TODO: query with PageRequest
//    fun getAllTasksWithPurposes(pageRequest: PageRequest<DomainTask>): Flow<Map<DomainPurpose, PageResponse<DomainTask>>>
    suspend fun update(vararg tasks: DomainTask): Unit
    suspend fun delete(vararg tasks: DomainTask): Unit
    suspend fun insert(vararg tasks: DomainTask): List<Long>
    suspend fun deleteAll(): Unit
}
