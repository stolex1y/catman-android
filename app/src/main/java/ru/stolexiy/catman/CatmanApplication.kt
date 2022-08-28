package ru.stolexiy.catman

import android.app.Application
import android.graphics.Color
import kotlinx.coroutines.*
import ru.stolexiy.catman.data.CategoryRepositoryImpl
import ru.stolexiy.catman.data.PurposeRepositoryImpl
import ru.stolexiy.catman.data.datasource.local.LocalDatabase
import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.model.Purpose
import ru.stolexiy.catman.domain.usecase.UseCases
import timber.log.Timber
import java.util.GregorianCalendar

class CatmanApplication: Application() {
    lateinit var localDatabase: LocalDatabase

    val categoryRepository get() = CategoryRepositoryImpl(localDatabase.categoryDao())
    val purposeRepository get() = PurposeRepositoryImpl(localDatabase.purposeDao())
//    val categoryWithPurposeRepository get() = CategoryWithPurposeRepositoryImpl(localDatabase.categoryWithPurposesDao())

    val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception.stackTraceToString())
    }
    val applicationScope = CoroutineScope(SupervisorJob() + coroutineExceptionHandler + Dispatchers.Main)
    lateinit var useCases: UseCases

    override fun onCreate() {
        super.onCreate()
        localDatabase = LocalDatabase.getInstance(this)
        useCases = UseCases(
            categoryRepository,
            purposeRepository,
//            categoryWithPurposeRepository
        )

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        fillDatabase()
    }

    private fun fillDatabase() {
        applicationScope.launch {
            categoryRepository.getAllCategoriesOnce().let { categories ->
                if (categories.isEmpty()) {
                    categoryRepository.insertCategory(
                        Category("Образование", this@CatmanApplication.resources.getColor(R.color.aquamarine)),
                        Category("Работа", this@CatmanApplication.resources.getColor(R.color.amaranth_pink))
                    )
                    categoryRepository.getAllCategoriesOnce().let { categories ->
                        if (categories.size >= 2) {
                            useCases.addPurposeToCategory(
                                Purpose(
                                    "Диплом",
                                    categories[0].id,
                                    GregorianCalendar(2023, 4, 31),
                                    progress = 10
                                ),
                                Purpose(
                                    "Стажировка",
                                    categories[1].id,
                                    GregorianCalendar(2022, 7, 28),
                                    progress = 27
                                )
                            )
                        }
                    }
                }
            }
            launch {
                categoryRepository.getAllCategoriesWithPurposes().collect {
                    Timber.d("init db $it")
                    cancel()
                }
            }
        }
    }
}