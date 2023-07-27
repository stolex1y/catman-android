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
import ru.stolexiy.catman.domain.repository.category.CategoryDeletingRepository
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.catman.ui.util.work.WorkUtils
import ru.stolexiy.catman.ui.util.work.AbstractWorker

@HiltWorker
class DeleteCategoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val gettingRepository: CategoryGettingRepository,
    private val deletingRepository: CategoryDeletingRepository,
) : AbstractWorker<Long, DomainCategory>(
    WorkUtils.DELETING_ENTITY_NOTIFICATION_ID,
    appContext.getString(R.string.category_deleting),
    "Category deleting",
    { action(gettingRepository, deletingRepository, it) },
    Long::class,
    appContext,
    workerParams
) {
    companion object {
        fun createWorkRequest(id: Long): OneTimeWorkRequest {
            return createWorkRequest<DeleteCategoryWorker, Long, DomainCategory>(id)
        }

        private suspend fun action(
            categoryGet: CategoryGettingRepository,
            categoryDelete: CategoryDeletingRepository,
            id: Long
        ): kotlin.Result<DomainCategory> {
            return runCatching {
                val deletedCategory = categoryGet.byId(id).first().getOrThrow()
                    ?: throw IllegalArgumentException()
                categoryDelete.byId(id)
                return@runCatching deletedCategory
            }
        }
    }
}
