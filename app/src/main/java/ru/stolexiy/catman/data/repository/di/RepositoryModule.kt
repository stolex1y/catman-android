package ru.stolexiy.catman.data.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.stolexiy.catman.data.repository.CategoriesWithPurposesRepositoryImpl
import ru.stolexiy.catman.data.repository.CategoryRepositoryImpl
import ru.stolexiy.catman.data.repository.ColorRepositoryImpl
import ru.stolexiy.catman.data.repository.PurposeRepositoryImpl
import ru.stolexiy.catman.data.repository.TaskRepositoryImpl
import ru.stolexiy.catman.domain.repository.category.CategoriesWithPurposesRepository
import ru.stolexiy.catman.domain.repository.category.CategoryRepository
import ru.stolexiy.catman.domain.repository.ColorRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeRepository
import ru.stolexiy.catman.domain.repository.TaskRepository

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun categoryRepository(i: CategoryRepositoryImpl): CategoryRepository

    @Binds
    fun purposeRepository(i: PurposeRepositoryImpl): PurposeRepository

    @Binds
    fun categoryWithPurposeRepository(
        i: CategoriesWithPurposesRepositoryImpl
    ): CategoriesWithPurposesRepository

    @Binds
    fun colorRepository(i: ColorRepositoryImpl): ColorRepository

    @Binds
    fun taskRepository(i: TaskRepositoryImpl): TaskRepository
}
