package ru.stolexiy.catman.data.repository.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.stolexiy.catman.data.datasource.local.di.LocalDatasourceNames
import ru.stolexiy.catman.data.repository.category.CategoryAddingRepositoryImpl
import ru.stolexiy.catman.data.repository.category.CategoryDeletingRepositoryImpl
import ru.stolexiy.catman.data.repository.category.CategoryGettingRepositoryImpl
import ru.stolexiy.catman.data.repository.category.CategoryGettingWithPurposesRepositoryImpl
import ru.stolexiy.catman.data.repository.category.CategoryUpdatingRepositoryImpl
import ru.stolexiy.catman.data.repository.color.ColorRepositoryImpl
import ru.stolexiy.catman.data.repository.purpose.PurposeAddingRepositoryImpl
import ru.stolexiy.catman.data.repository.purpose.PurposeDeletingRepositoryImpl
import ru.stolexiy.catman.data.repository.purpose.PurposeGettingByCategoryRepositoryImpl
import ru.stolexiy.catman.data.repository.purpose.PurposeGettingRepositoryImpl
import ru.stolexiy.catman.data.repository.purpose.PurposeGettingWithTasksRepositoryImpl
import ru.stolexiy.catman.data.repository.purpose.PurposeUpdatingRepositoryImpl
import ru.stolexiy.catman.data.repository.task.TaskAddingRepositoryImpl
import ru.stolexiy.catman.data.repository.task.TaskDeletingRepositoryImpl
import ru.stolexiy.catman.data.repository.task.TaskGettingByPurposeRepositoryImpl
import ru.stolexiy.catman.data.repository.task.TaskGettingFinishedRepositoryImpl
import ru.stolexiy.catman.data.repository.task.TaskGettingRepositoryImpl
import ru.stolexiy.catman.data.repository.task.TaskUpdatingRepositoryImpl
import ru.stolexiy.catman.domain.repository.TransactionProvider
import ru.stolexiy.catman.domain.repository.category.CategoryAddingRepository
import ru.stolexiy.catman.domain.repository.category.CategoryDeletingRepository
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.catman.domain.repository.category.CategoryGettingWithPurposesRepository
import ru.stolexiy.catman.domain.repository.category.CategoryUpdatingRepository
import ru.stolexiy.catman.domain.repository.color.ColorRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeAddingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeDeletingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingByCategoryRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingWithTasksRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeUpdatingRepository
import ru.stolexiy.catman.domain.repository.task.TaskAddingRepository
import ru.stolexiy.catman.domain.repository.task.TaskDeletingRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingByPurposeRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingFinishedRepository
import ru.stolexiy.catman.domain.repository.task.TaskGettingRepository
import ru.stolexiy.catman.domain.repository.task.TaskUpdatingRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {
    @Binds
    fun categoryUpdatingRepository(i: CategoryUpdatingRepositoryImpl): CategoryUpdatingRepository

    @Binds
    fun categoryAddingRepository(i: CategoryAddingRepositoryImpl): CategoryAddingRepository

    @Binds
    fun categoryDeletingRepository(i: CategoryDeletingRepositoryImpl): CategoryDeletingRepository

    @Binds
    fun categoryGettingRepository(i: CategoryGettingRepositoryImpl): CategoryGettingRepository

    @Binds
    fun categoryGettingWithPurposesRepository(i: CategoryGettingWithPurposesRepositoryImpl): CategoryGettingWithPurposesRepository


    @Binds
    fun purposeUpdatingRepository(i: PurposeUpdatingRepositoryImpl): PurposeUpdatingRepository

    @Binds
    fun purposeAddingRepository(i: PurposeAddingRepositoryImpl): PurposeAddingRepository

    @Binds
    fun purposeDeletingRepository(i: PurposeDeletingRepositoryImpl): PurposeDeletingRepository

    @Binds
    fun purposeGettingWithTasksRepository(i: PurposeGettingWithTasksRepositoryImpl): PurposeGettingWithTasksRepository

    @Binds
    fun purposeGettingRepository(i: PurposeGettingRepositoryImpl): PurposeGettingRepository

    @Binds
    fun purposeGettingByCategoryRepository(i: PurposeGettingByCategoryRepositoryImpl): PurposeGettingByCategoryRepository


    @Binds
    fun colorRepository(i: ColorRepositoryImpl): ColorRepository


    @Binds
    fun taskGettingRepository(i: TaskGettingRepositoryImpl): TaskGettingRepository

    @Binds
    fun taskUpdatingRepository(i: TaskUpdatingRepositoryImpl): TaskUpdatingRepository

    @Binds
    fun taskDeletingRepository(i: TaskDeletingRepositoryImpl): TaskDeletingRepository

    @Binds
    fun taskAddingRepository(i: TaskAddingRepositoryImpl): TaskAddingRepository

    @Binds
    fun taskGettingByPurposeRepository(i: TaskGettingByPurposeRepositoryImpl): TaskGettingByPurposeRepository

    @Binds
    fun taskGettingFinishedRepository(i: TaskGettingFinishedRepositoryImpl): TaskGettingFinishedRepository

    companion object {
        @Provides
        @Singleton
        fun transactionProvider(
            @Named(LocalDatasourceNames.LOCAL_TRANSACTION_PROVIDER) localTransactionProvider: TransactionProvider,
        ): TransactionProvider {
            return object : TransactionProvider {
                override suspend fun <T> runInTransaction(block: suspend () -> T): T {
                    return localTransactionProvider.runInTransaction(block)
                }
            }
        }
    }
}
