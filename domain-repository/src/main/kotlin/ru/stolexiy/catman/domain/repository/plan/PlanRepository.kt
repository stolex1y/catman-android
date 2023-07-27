package ru.stolexiy.catman.domain.repository.plan

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPlan
import java.time.ZonedDateTime

interface PlanRepository {
    fun getToday(id: Long): Flow<DomainPlan?>
    fun getAll(): Flow<List<DomainPlan>>
    fun getAllByDate(calendar: ZonedDateTime): Flow<List<DomainPlan>>
    suspend fun update(vararg plans: DomainPlan): Unit
    suspend fun delete(vararg plan: DomainPlan): Unit
    suspend fun insert(vararg plan: DomainPlan): List<Long>
    suspend fun deleteAll(): Unit
}
