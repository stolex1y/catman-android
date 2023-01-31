package ru.stolexiy.catman.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainColor

interface ColorRepository {
    fun getColor(color: Int): Flow<DomainColor?>
    fun getAllColors(): Flow<List<DomainColor>>
    suspend fun updateColor(vararg colors: DomainColor)
    suspend fun deleteColor(vararg colors: DomainColor)
    suspend fun deleteAllColors()
    suspend fun createColor(vararg colors: DomainColor)
}