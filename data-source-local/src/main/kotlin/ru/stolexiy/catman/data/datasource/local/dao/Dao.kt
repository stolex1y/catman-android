package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

abstract class Dao<T> {

    @Update
    abstract suspend fun update(vararg entities: T)

    @Insert
    abstract suspend fun insert(vararg entities: T): List<Long>

    @Delete
    abstract suspend fun delete(vararg entities: T): Int

    abstract suspend fun deleteAll(): Int
}
