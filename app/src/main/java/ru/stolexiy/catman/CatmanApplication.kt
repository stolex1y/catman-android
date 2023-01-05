package ru.stolexiy.catman

import android.app.Application
import kotlinx.coroutines.*
import ru.stolexiy.catman.data.CategoryRepositoryImpl
import ru.stolexiy.catman.data.PurposeRepositoryImpl
import ru.stolexiy.catman.data.datasource.local.LocalDatabase
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.UseCases
import timber.log.Timber
import java.util.GregorianCalendar

class CatmanApplication: Application() {
    lateinit var localDatabase: LocalDatabase

    val categoryRepository get() = CategoryRepositoryImpl(localDatabase.categoryDao())
    val purposeRepository get() = PurposeRepositoryImpl(localDatabase.purposeDao())
//    val categoryWithPurposeRepository get() = CategoryWithPurposeRepositoryImpl(localDatabase.categoryWithPurposesDao())

    private val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
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
                        DomainCategory("Образование", 0x7FFFD4),
                        DomainCategory("Работа", 0x66CC66)
                    )
                    categoryRepository.getAllCategoriesOnce().let { categories ->
                        if (categories.size >= 2) {
                            useCases.purposeCommon(
                                DomainPurpose(
                                    "Диплом",
                                    categories[0].id,
                                    GregorianCalendar(2023, 4, 31),
                                    progress = 10
                                ),
                                DomainPurpose(
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