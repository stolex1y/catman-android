package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.model.Purpose

interface PurposeRepository {
    fun getPurpose(id: Long): Flow<Purpose>
    fun getAllPurposes(): Flow<List<Purpose>>
    suspend fun getAllPurposesOnce(): List<Purpose>
    suspend fun getPurposeOnce(id: Long): Purpose
    fun getAllPurposesByCategoryOrderByPriority(categoryId: Long): Flow<List<Purpose>>

    suspend fun getAllPurposesByCategoryOrderByPriorityOnce(categoryId: Long): List<Purpose>
    fun getPurposeWithTasks(id: Long): Flow<Purpose>
    suspend fun updatePurpose(vararg purposes: Purpose)
    suspend fun insertPurpose(vararg purposes: Purpose)
    suspend fun deletePurpose(vararg purposes: Purpose)
    suspend fun deleteAllPurposes()
}