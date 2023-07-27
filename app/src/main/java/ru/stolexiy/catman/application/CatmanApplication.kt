package ru.stolexiy.catman.application

import android.app.NotificationManager
import android.os.Process
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.stolexiy.catman.BuildConfig
import ru.stolexiy.catman.core.di.CoroutineModule
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.category.CategoryAddingRepository
import ru.stolexiy.catman.domain.repository.category.CategoryDeletingRepository
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.catman.domain.repository.category.CategoryGettingWithPurposesRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeAddingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeDeletingRepository
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.catman.domain.usecase.color.ColorGettingUseCase
import ru.stolexiy.catman.ui.util.notification.NotificationChannels.initChannels
import ru.stolexiy.common.DateUtils.getDayLastMoment
import timber.log.Timber
import java.time.LocalDate
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
    lateinit var getCategory: CategoryGettingRepository

    @Inject
    lateinit var deleteCategory: CategoryDeletingRepository

    @Inject
    lateinit var addCategory: CategoryAddingRepository

    @Inject
    lateinit var getPurpose: PurposeGettingRepository

    @Inject
    lateinit var deletePurpose: PurposeDeletingRepository

    @Inject
    lateinit var addPurpose: PurposeAddingRepository

    @Inject
    lateinit var getCategoryWithPurpose: CategoryGettingWithPurposesRepository

    @Inject
    lateinit var getColor: ColorGettingUseCase

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

        notificationManager.getOrNull()?.initChannels(this)

        applicationScope.launch {
            fillDatabase()
        }

        Timber.d("appliction created")
    }

    private suspend fun clearDatabase() {
        deleteCategory.all()
    }

    private suspend fun fillDatabase() {
        var categories = getCategory.all().first().getOrThrow()
        val colors = getColor.all().first().getOrThrow()
        if (categories.isEmpty()) {
            val category1 = DomainCategory("Образование", colors.first().argb, 0L, "")
            val category2 = DomainCategory("Работа", colors.last().argb, 0L, "")
            addCategory(category1).onFailure { throw it }
            addCategory(category2).onFailure { throw it }
            categories = getCategory.all().first().getOrNull() ?: return
            val purpose1 = DomainPurpose(
                "Диплом",
                categories[0].id,
                LocalDate.of(2023, 7, 31).getDayLastMoment(),
                progress = 0.1,
                description = "",
                isFinished = false,
                id = 0L,
                priority = 0
            )
            val purpose2 = DomainPurpose(
                "Стажировка",
                categories[1].id,
                LocalDate.of(2023, 7, 30).getDayLastMoment(),
                progress = 0.27,
                description = "",
                isFinished = false,
                id = 0L,
                priority = 0
            )
            addPurpose(purpose1).onFailure { throw it }
            addPurpose(purpose2).onFailure { throw it }
            getCategoryWithPurpose.all().first().getOrThrow().let {
                Timber.d("all categories with purposes $it")
            }
        }
    }

    companion object {
        private const val GLOBAL_TAG: String = "AY"
    }
}
