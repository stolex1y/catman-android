package ru.stolexiy.catman.data.datasource.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.stolexiy.catman.data.datasource.local.dao.CategoriesWithPurposesDao
import ru.stolexiy.catman.data.datasource.local.dao.CategoryDao
import ru.stolexiy.catman.data.datasource.local.dao.ColorDao
import ru.stolexiy.catman.data.datasource.local.dao.PurposeDao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.ColorEntity
import ru.stolexiy.catman.data.datasource.local.model.PlanEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity

@Database(
    entities = [
        CategoryEntity::class,
        PurposeEntity::class,
        TaskEntity::class,
        PlanEntity::class,
        ColorEntity::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun purposeDao(): PurposeDao
    abstract fun categoriesWithPurposesDao(): CategoriesWithPurposesDao
    abstract fun colorDao(): ColorDao

    companion object {
        const val DATABASE_NAME = "catman-db"

        @Volatile
        var instance: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): LocalDatabase =
            Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(LocalDatabaseCallback())
                .build()
    }
}