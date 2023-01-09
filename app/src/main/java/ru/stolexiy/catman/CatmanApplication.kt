package ru.stolexiy.catman

import android.app.Application
import android.widget.Toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import ru.stolexiy.catman.data.datasource.local.LocalDatabase
import ru.stolexiy.catman.data.repository.CategoryRepositoryImpl
import ru.stolexiy.catman.data.repository.PurposeRepositoryImpl
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import ru.stolexiy.catman.domain.usecase.AllCategoriesWithPurposes
import ru.stolexiy.catman.domain.usecase.CategoryCrud
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import timber.log.Timber
import java.util.GregorianCalendar

class CatmanApplication: Application() {
    lateinit var localDatabase: LocalDatabase

    lateinit var categoryRepository: CategoryRepository
    lateinit var purposeRepository: PurposeRepository
    lateinit var categoryCrud: CategoryCrud
    lateinit var purposeCrud: PurposeCrud
    lateinit var allCategoriesWithPurposes: AllCategoriesWithPurposes

    private val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Toast.makeText(applicationContext, "Exception...", Toast.LENGTH_LONG).show()
        Timber.e(exception.stackTraceToString())
    }
    val applicationScope = CoroutineScope(SupervisorJob() + coroutineExceptionHandler + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        localDatabase = LocalDatabase.getInstance(this)
        categoryRepository = CategoryRepositoryImpl(localDatabase.categoryDao())
        purposeRepository = PurposeRepositoryImpl(localDatabase.purposeDao())
        categoryCrud = CategoryCrud(Dispatchers.IO, categoryRepository)
        purposeCrud = PurposeCrud(Dispatchers.IO, purposeRepository, categoryRepository)
        allCategoriesWithPurposes = AllCategoriesWithPurposes(Dispatchers.IO, categoryRepository)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

//        applicationScope.launch {
//            categoryCrud.clear()
        fillDatabase()
//        }
    }

    private fun fillDatabase() {
        applicationScope.launch {
            categoryRepository.getAllCategoriesOnce().let { categories ->
                if (categories.isEmpty()) {
                    categoryCrud.create(DomainCategory("Образование", 0x7FFFD4))
                    categoryCrud.create(DomainCategory("Работа", 0x66CC66))
                    categoryCrud.getAll().take(1).collect { categories ->
                        if (categories.size >= 2) {
                            purposeCrud.create(
                                DomainPurpose(
                                    "Диплом",
                                    categories[0].id,
                                    GregorianCalendar(2023, 4, 31),
                                    progress = 10
                                )
                            )
                            purposeCrud.create(
                                DomainPurpose(
                                    "Стажировка",
                                    categories[1].id,
                                    GregorianCalendar(2023, 7, 28),
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