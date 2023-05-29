package ru.stolexiy.catman.ui.util.work.category

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.usecase.category.CategoryDeletingUseCase
import ru.stolexiy.catman.domain.usecase.category.CategoryGettingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils
import ru.stolexiy.catman.ui.util.work.WorkUtils.DELETE_CATEGORY_NOTIFICATION_ID

@HiltWorker
class DeleteCategoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getCategory: CategoryGettingUseCase,
    private val deleteCategory: CategoryDeletingUseCase
) : AbstractWorker<Long, DomainCategory?>(Long::class, appContext, workerParams) {

    override val notificationId: Int = DELETE_CATEGORY_NOTIFICATION_ID
    override val notificationMsg: String = applicationContext.getString(R.string.category_deleting)
    override val workName: String = DELETE_CATEGORY_ID

    companion object {
        private const val DELETE_CATEGORY_ID = "Category deleting"

        fun createWorkRequest(deletingCategoryId: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DeleteCategoryWorker>()
                .addTag(DELETE_CATEGORY_ID)
                .setInputData(WorkUtils.serialize(deletingCategoryId))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun calculate(inputArg: Long): kotlin.Result<DomainCategory?> {
        return runCatching {
            val deletingCategory = CoroutineScope(currentCoroutineContext()).async {
                getCategory.byId(inputArg).first().getOrThrow()
            }
            deleteCategory.byId(inputArg)
            return@runCatching deletingCategory.await()
        }
    }
}
