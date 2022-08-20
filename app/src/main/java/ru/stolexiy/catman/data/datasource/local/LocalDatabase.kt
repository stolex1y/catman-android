package ru.stolexiy.catman.data.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.stolexiy.catman.data.datasource.local.dao.LocalCategoryDao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PlanEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity

const val DATABASE_NAME = "catman-db"

@Database(
    entities = [
        CategoryEntity::class,
        PurposeEntity::class,
        TaskEntity::class,
        PlanEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun categoryDao(): LocalCategoryDao

    @Volatile var instance: LocalDatabase? = null

    fun getInstance(context: Context): LocalDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }

    private fun buildDatabase(context: Context): LocalDatabase =
        Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE_NAME).build()
}