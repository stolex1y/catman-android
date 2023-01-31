package ru.stolexiy.catman

import android.app.Application
import android.os.Build
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.data.datasource.local.LocalDatabase
import ru.stolexiy.catman.data.repository.CategoriesWithPurposesRepositoryImpl
import ru.stolexiy.catman.data.repository.CategoryRepositoryImpl
import ru.stolexiy.catman.data.repository.PurposeRepositoryImpl
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.CategoriesWithPurposesRepository
import ru.stolexiy.catman.domain.repository.CategoryRepository
import ru.stolexiy.catman.domain.repository.PurposeRepository
import ru.stolexiy.catman.domain.usecase.AllCategoriesWithPurposes
import ru.stolexiy.catman.domain.usecase.CategoryCrud
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.util.notification.NotificationChannels
import timber.log.Timber
import java.util.GregorianCalendar

class CatmanApplication: Application() {
    lateinit var localDatabase: LocalDatabase

    lateinit var categoryRepository: CategoryRepository
    lateinit var purposeRepository: PurposeRepository
    lateinit var categoriesWithPurposesRepository: CategoriesWithPurposesRepository
    lateinit var categoryCrud: CategoryCrud
    lateinit var purposeCrud: PurposeCrud
    lateinit var allCategoriesWithPurposes: AllCategoriesWithPurposes

    val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception.stackTraceToString())
    }
    val applicationScope = CoroutineScope(SupervisorJob() + coroutineExceptionHandler + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        localDatabase = LocalDatabase.getInstance(this)
        categoryRepository = CategoryRepositoryImpl(localDatabase.categoryDao())
        purposeRepository = PurposeRepositoryImpl(localDatabase.purposeDao())
        categoriesWithPurposesRepository = CategoriesWithPurposesRepositoryImpl(localDatabase.categoriesWithPurposesDao())
        categoryCrud = CategoryCrud(categoryRepository)
        purposeCrud = PurposeCrud(purposeRepository, categoryRepository)
        allCategoriesWithPurposes = AllCategoriesWithPurposes(categoriesWithPurposesRepository)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        applicationScope.launch {
            withContext(Dispatchers.Default) {
//                categoryCrud.clear()
                fillDatabase()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannels.initChannels(this)
        }
    }

    private suspend fun clearDatabase() {
        categoryCrud.clear()
    }

    private suspend fun fillDatabase() {
        var categories = categoryRepository.getAllCategories().first()
        if (categories.isEmpty()) {
            val category1 = DomainCategory("Образование", 0x7FFFD4)
            val category2 = DomainCategory("Работа", 0x66CC66)
            categoryCrud.create(category1).onFailure { throw it }
            categoryCrud.create(category2).onFailure { throw it }
            categories = categoryCrud.getAll().first().getOrNull() ?: return
            val purpose1 = DomainPurpose(
                "Диплом",
                categories[0].id,
                GregorianCalendar(2023, 4, 31),
                progress = 10
            )
            val purpose2 = DomainPurpose(
                "Стажировка",
                categories[1].id,
                GregorianCalendar(2023, 7, 28),
                progress = 27
            )
            purposeCrud.create(purpose1).onFailure { throw it }
            purposeCrud.create(purpose2).onFailure { throw it }
        }
        allCategoriesWithPurposes().first().getOrNull()?.let {
            Timber.d("init db $it")
        }
        localDatabase.categoryDao().getAllWithPurposes().first().let {
            Timber.d("all categories with purposes $it")
        }
    }
}