package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

abstract class Dao<T> {

    protected abstract fun getAllNotDistinct(): Flow<List<T>>

    fun getAll(): Flow<List<T>> = getAllNotDistinct().distinctUntilChanged()

    protected abstract fun getNotDistinct(id: Long): Flow<T>

    fun get(id: Long): Flow<T> = getNotDistinct(id).distinctUntilChanged()

    @Update
    abstract suspend fun update(entity: T)

    @Insert
    abstract suspend fun insert(entity: T): Long

    @Delete
    abstract suspend fun delete(entity: T)
}