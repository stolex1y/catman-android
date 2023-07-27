package ru.stolexiy.catman.ui.util.work.category

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.repository.category.CategoryAddingRepository
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class AddCategoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val addingRepository: CategoryAddingRepository,
) : AbstractWorker<DomainCategory, Long>(
    WorkUtils.ADDING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.category_adding),
    "Category adding",
    { action(addingRepository, it) },
    DomainCategory::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(entity: DomainCategory): OneTimeWorkRequest {
            return createWorkRequest<AddCategoryWorker, DomainCategory, Long>(entity)
        }

        private suspend fun action(
            categoryAdd: CategoryAddingRepository,
            category: DomainCategory
        ): kotlin.Result<Long> {
            return categoryAdd(category).map { it.first() }
        }
    }
}
