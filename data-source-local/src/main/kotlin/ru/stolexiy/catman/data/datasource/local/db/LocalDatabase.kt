package ru.stolexiy.catman.data.datasource.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.stolexiy.catman.data.datasource.local.BuildConfig
import ru.stolexiy.catman.data.datasource.local.dao.ColorDao
import ru.stolexiy.catman.data.datasource.local.dao.category.CategoryCrudDao
import ru.stolexiy.catman.data.datasource.local.dao.category.CategoryWithPurposesDao
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeCrudDao
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeWithTasksDao
import ru.stolexiy.catman.data.datasource.local.dao.task.TaskCrudDao
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.ColorEntity
import ru.stolexiy.catman.data.datasource.local.model.PlanEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.datasource.local.model.TaskEntity
import timber.log.Timber
import java.util.concurrent.Executors

@Database(
    entities = [
        CategoryEntity::class,
        PurposeEntity::class,
        TaskEntity::class,
        PlanEntity::class,
        ColorEntity::class
    ],
    version = 11,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryCrudDao
    abstract fun categoriesWithPurposesDao(): CategoryWithPurposesDao
    abstract fun purposeDao(): PurposeCrudDao
    abstract fun purposeWithTasksDao(): PurposeWithTasksDao
    abstract fun colorDao(): ColorDao
    abstract fun taskCrudDao(): TaskCrudDao

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

        private fun Builder<LocalDatabase>.setLoggingQueryCallback() {
            if (BuildConfig.DEBUG) {
                setQueryCallback(object : QueryCallback {
                    override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
                        Timber.v("query: $sqlQuery; args: ${bindArgs.joinToString(", ")}")
                    }
                }, Executors.newSingleThreadExecutor())
            }
        }
    }
}