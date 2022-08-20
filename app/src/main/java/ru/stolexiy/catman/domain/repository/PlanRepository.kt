package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Purpose

interface PlanRepository {
    fun getTodayPlan(id: Long): Flow<Purpose>
    fun getAllPurposes(): Flow<List<Purpose>>
    fun updatePurpose(purpose: Purpose): Flow<Purpose>
    suspend fun deletePurpose(id: Long)
}