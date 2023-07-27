package ru.stolexiy.catman.ui.util.work.category

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.catman.domain.repository.category.CategoryUpdatingRepository
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class UpdateCategoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val gettingRepository: CategoryGettingRepository,
    private val updatingRepository: CategoryUpdatingRepository,
) : AbstractWorker<DomainCategory, DomainCategory>(
    WorkUtils.UPDATING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.category_updating),
    "Category updating",
    { action(gettingRepository, updatingRepository, it) },
    DomainCategory::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(entity: DomainCategory): OneTimeWorkRequest {
            return createWorkRequest<UpdateCategoryWorker, DomainCategory, DomainCategory>(entity)
        }

        private suspend fun action(
            categoryGet: CategoryGettingRepository,
            categoryUpdate: CategoryUpdatingRepository,
            category: DomainCategory
        ): kotlin.Result<DomainCategory> = runCatching {
            val untilUpdate = categoryGet.byId(category.id).first().getOrThrow()
                ?: throw IllegalArgumentException()
            categoryUpdate(category).getOrThrow()
            return@runCatching untilUpdate
        }
    }
}
