package ru.stolexiy.catman

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.AllCategoriesWithPurposes
import ru.stolexiy.catman.domain.usecase.CategoryCrud
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.util.notification.NotificationChannels
import timber.log.Timber
import java.util.GregorianCalendar
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class CatmanApplication : Application(), Configuration.Provider {

    @Named(CoroutineModule.APPLICATION_SCOPE)
    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var categoryCrud: CategoryCrud

    @Inject
    lateinit var purposeCrud: PurposeCrud

    @Inject
    lateinit var allCategoriesWithPurposes: AllCategoriesWithPurposes

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

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
        var categories = categoryCrud.getAll().first().getOrThrow()
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
        allCategoriesWithPurposes.invoke().first().getOrThrow().let {
            Timber.d("all categories with purposes $it")
        }
    }
}
