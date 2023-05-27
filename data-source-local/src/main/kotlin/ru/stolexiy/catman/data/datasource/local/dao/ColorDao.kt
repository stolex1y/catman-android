package ru.stolexiy.catman.data.datasource.local.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.data.datasource.local.model.ColorEntity

@androidx.room.Dao
abstract class ColorDao : Dao<ColorEntity>() {

    @Query("SELECT * FROM colors")
    abstract fun getAll(): Flow<List<ColorEntity>>

    @Query("SELECT * FROM colors WHERE color_argb = :color")
    abstract fun get(color: Int): Flow<ColorEntity?>

    @Query("DELETE FROM colors")
    abstract override suspend fun deleteAll()
}
