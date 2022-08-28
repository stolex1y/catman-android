package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Plan
import ru.stolexiy.catman.domain.model.Purpose
import java.util.*

interface PlanRepository {
    fun getTodayPlan(id: Long): Flow<Plan>
    fun getAllPlans(): Flow<List<Plan>>

    fun getAllPlansByDate(calendar: Calendar): Flow<List<Plan>>
    suspend fun updatePlan(vararg plans: Plan)
    suspend fun deletePlan(vararg plan: Plan)
    suspend fun insertPlan(vararg plan: Plan)
    suspend fun deleteAllPlans()
}