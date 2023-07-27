package ru.stolexiy.catman.domain.repository.color

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainColor

interface ColorRepository {
    fun get(color: Int): Flow<DomainColor?>
    fun getAll(): Flow<List<DomainColor>>
    suspend fun update(vararg colors: DomainColor): Unit
    suspend fun delete(vararg colors: Int): Unit
    suspend fun deleteAll(): Unit
    suspend fun insert(vararg colors: DomainColor): Unit
}
