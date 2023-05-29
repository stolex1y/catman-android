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
import ru.stolexiy.catman.domain.usecase.category.CategoryGettingUseCase
import ru.stolexiy.catman.domain.usecase.category.CategoryUpdatingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils

@HiltWorker
class UpdateCategoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getCategory: CategoryGettingUseCase,
    private val updateCategory: CategoryUpdatingUseCase
) : AbstractWorker<DomainCategory, DomainCategory?>(
    DomainCategory::class,
    appContext,
    workerParams
) {

    override val notificationId: Int = WorkUtils.UPDATE_PURPOSE_NOTIFICATION_ID
    override val notificationMsg: String = applicationContext.getString(R.string.category_updating)
    override val workName: String = UPDATE_PURPOSE_TAG

    companion object {
        const val UPDATE_PURPOSE_TAG = "Category updating"

        fun createWorkRequest(updatingCategory: DomainCategory): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<UpdateCategoryWorker>()
                .setInputData(WorkUtils.serialize(updatingCategory))
                .addTag(UPDATE_PURPOSE_TAG)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun calculate(inputArg: DomainCategory): kotlin.Result<DomainCategory?> {
        return runCatching {
            val untilUpdate = CoroutineScope(currentCoroutineContext()).async {
                getCategory.byId(inputArg.id).first().getOrThrow()
            }
            updateCategory(inputArg)
            untilUpdate.await()
        }
    }
}
