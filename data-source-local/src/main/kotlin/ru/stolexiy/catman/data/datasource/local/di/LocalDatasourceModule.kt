package ru.stolexiy.catman.data.datasource.local.di

import android.content.Context
import androidx.room.withTransaction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import ru.stolexiy.catman.data.datasource.local.dao.ColorDao
import ru.stolexiy.catman.data.datasource.local.dao.category.CategoryCrudDao
import ru.stolexiy.catman.data.datasource.local.dao.category.CategoryWithPurposesDao
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeCrudDao
import ru.stolexiy.catman.data.datasource.local.dao.purpose.PurposeWithTasksDao
import ru.stolexiy.catman.data.datasource.local.dao.task.TaskCrudDao
import ru.stolexiy.catman.data.datasource.local.db.LocalDatabase
import ru.stolexiy.catman.domain.repository.TransactionProvider
import ru.stolexiy.common.di.SingleThreadDispatcherProvider
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LocalDatasourceModule {

    companion object {
        @Provides
        @Singleton
        fun localDatabase(@ApplicationContext context: Context): LocalDatabase {
            return LocalDatabase.getInstance(context)
        }

        @Provides
        @Singleton
        fun localCategoryDao(localDatabase: LocalDatabase): CategoryCrudDao {
            return localDatabase.categoryDao()
        }

        @Provides
        @Singleton
        fun localPurposeDao(localDatabase: LocalDatabase): PurposeCrudDao {
            return localDatabase.purposeDao()
        }

        @Provides
        @Singleton
        fun localCategoriesWithPurposesDao(localDatabase: LocalDatabase): CategoryWithPurposesDao {
            return localDatabase.categoriesWithPurposesDao()
        }

        @Provides
        @Singleton
        fun localColorDao(localDatabase: LocalDatabase): ColorDao {
            return localDatabase.colorDao()
        }

        @Provides
        @Singleton
        fun localTaskCrudDao(localDatabase: LocalDatabase): TaskCrudDao {
            return localDatabase.taskCrudDao()
        }

        @Provides
        @Singleton
        fun localPurposeWithTasksDao(localDatabase: LocalDatabase): PurposeWithTasksDao {
            return localDatabase.purposeWithTasksDao()
        }

        @Provides
        @Singleton
        @Named(LocalDatasourceNames.TRANSACTION_THREAD_DISPATCHER)
        fun transactionThreadDispatcher(singleThreadDispatcherProvider: SingleThreadDispatcherProvider): CoroutineDispatcher {
            return singleThreadDispatcherProvider.get("DB transaction")
        }

        @Provides
        @Singleton
        @Named(LocalDatasourceNames.LOCAL_TRANSACTION_PROVIDER)
        fun localTransactionProvider(
            localDatabase: LocalDatabase,
        ): TransactionProvider {
            return object : TransactionProvider {
                override suspend fun <T> runInTransaction(block: suspend () -> T): T {
                    return localDatabase.withTransaction {
                        block()
                    }
                }
            }
        }
    }
}
