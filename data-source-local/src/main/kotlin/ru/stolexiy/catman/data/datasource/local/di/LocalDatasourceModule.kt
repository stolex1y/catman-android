package ru.stolexiy.catman.data.datasource.local.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.stolexiy.catman.data.datasource.local.dao.CategoriesWithPurposesDao
import ru.stolexiy.catman.data.datasource.local.dao.CategoryDao
import ru.stolexiy.catman.data.datasource.local.dao.ColorDao
import ru.stolexiy.catman.data.datasource.local.dao.PurposeDao
import ru.stolexiy.catman.data.datasource.local.db.LocalDatabase
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
        fun localCategoryDao(localDatabase: LocalDatabase): CategoryDao {
            return localDatabase.categoryDao()
        }

        @Provides
        @Singleton
        fun localPurposeDao(localDatabase: LocalDatabase): PurposeDao {
            return localDatabase.purposeDao()
        }

        @Provides
        @Singleton
        fun localCategoriesWithPurposesDao(localDatabase: LocalDatabase): CategoriesWithPurposesDao {
            return localDatabase.categoriesWithPurposesDao()
        }

        @Provides
        @Singleton
        fun localColorDao(localDatabase: LocalDatabase): ColorDao {
            return localDatabase.colorDao()
        }
    }
}
