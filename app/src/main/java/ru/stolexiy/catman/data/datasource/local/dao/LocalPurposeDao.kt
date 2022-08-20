package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity

abstract class LocalPurposeDao : Dao<PurposeEntity>() {

    @Query("SELECT * FROM purposes")
    abstract override fun getAllNotDistinct(): Flow<List<PurposeEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    abstract override fun getNotDistinct(id: Long): Flow<PurposeEntity>

}