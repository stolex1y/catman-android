package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@androidx.room.Dao
abstract class Dao<T> {

    protected abstract fun getAllNotDistinct(): Flow<List<T>>

    fun getAll(): Flow<List<T>> = getAllNotDistinct().distinctUntilChanged()
    abstract fun getAllOnce(): List<T>

    protected abstract fun getNotDistinct(id: Long): Flow<T>
    abstract fun getOnce(id: Long): T

    fun get(id: Long): Flow<T> = getNotDistinct(id).distinctUntilChanged()

    @Update
    abstract suspend fun update(vararg entities: T)

    @Insert
    abstract suspend fun insert(vararg entities: T)

    @Delete
    abstract suspend fun delete(vararg entities: T)

    abstract suspend fun deleteAll()
}