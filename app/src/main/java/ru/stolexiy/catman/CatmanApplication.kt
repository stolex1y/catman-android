package ru.stolexiy.catman

import android.app.NotificationManager
import android.os.Build
import android.os.Process
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.core.model.DefaultColors
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.category.CategoryAddingUseCase
import ru.stolexiy.catman.domain.usecase.category.CategoryDeletingUseCase
import ru.stolexiy.catman.domain.usecase.category.CategoryGettingUseCase
import ru.stolexiy.catman.domain.usecase.category.CategoryWithPurposeGettingUseCase
import ru.stolexiy.catman.domain.usecase.purpose.PurposeAddingUseCase
import ru.stolexiy.catman.domain.usecase.purpose.PurposeDeletingUseCase
import ru.stolexiy.catman.domain.usecase.purpose.PurposeGettingUseCase
import ru.stolexiy.catman.ui.util.notification.NotificationChannels.initChannels
import timber.log.Timber
import java.util.GregorianCalendar
import java.util.Optional
import javax.inject.Inject
import javax.inject.Named
import kotlin.jvm.optionals.getOrNull

@HiltAndroidApp
class CatmanApplication : BaseApplication() {

    @Named(CoroutineModule.APPLICATION_SCOPE)
    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var getCategory: CategoryGettingUseCase

    @Inject
    lateinit var deleteCategory: CategoryDeletingUseCase

    @Inject
    lateinit var addCategory: CategoryAddingUseCase

    @Inject
    lateinit var getPurpose: PurposeGettingUseCase

    @Inject
    lateinit var deletePurpose: PurposeDeletingUseCase

    @Inject
    lateinit var addPurpose: PurposeAddingUseCase

    @Inject
    lateinit var getCategoryWithPurpose: CategoryWithPurposeGettingUseCase

    @Inject
    lateinit var notificationManager: Optional<NotificationManager>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(
                        priority, "[$GLOBAL_TAG] " +
                                "$tag | ${Process.getElapsedCpuTime()} ms", message, t
                    )
                }
            })
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.getOrNull()?.initChannels(this)
        }

        applicationScope.launch {
            fillDatabase()
        }

        Timber.d("appliction created")
    }

    private suspend fun clearDatabase() {
        deleteCategory.all()
    }

    private suspend fun fillDatabase() {
        var categories = getCategory.all().first().onFailure {
            throw it
        }.getOrThrow()
        if (categories.isEmpty()) {
            val category1 = DomainCategory("Образование", DefaultColors.AMARANTH_PINK.rgba)
            val category2 = DomainCategory("Работа", DefaultColors.AQUAMARINE.rgba)
            addCategory(category1).onFailure { throw it }
            addCategory(category2).onFailure { throw it }
            categories = getCategory.all().first().getOrNull() ?: return
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
            addPurpose(purpose1).onFailure { throw it }
            addPurpose(purpose2).onFailure { throw it }
        }
        getCategoryWithPurpose.all().first().getOrThrow().let {
            Timber.d("all categories with purposes $it")
        }
    }

    companion object {
        private const val GLOBAL_TAG: String = "AY"
    }
}
