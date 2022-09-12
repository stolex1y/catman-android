package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainPlan
import java.util.*

interface PlanRepository {
    fun getTodayPlan(id: Long): Flow<DomainPlan>
    fun getAllPlans(): Flow<List<DomainPlan>>

    fun getAllPlansByDate(calendar: Calendar): Flow<List<DomainPlan>>
    suspend fun updatePlan(vararg plans: DomainPlan)
    suspend fun deletePlan(vararg plan: DomainPlan)
    suspend fun insertPlan(vararg plan: DomainPlan)
    suspend fun deleteAllPlans()
}