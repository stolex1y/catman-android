package ru.stolexiy.catman.ui.util.work.category

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.usecase.category.CategoryAddingUseCase
import ru.stolexiy.catman.ui.util.work.AbstractWorker
import ru.stolexiy.catman.ui.util.work.WorkUtils
import ru.stolexiy.catman.ui.util.work.WorkUtils.ADD_CATEGORY_NOTIFICATION_ID

@HiltWorker
class AddCategoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val addCategory: CategoryAddingUseCase,
) : AbstractWorker<DomainCategory, Long>(DomainCategory::class, appContext, workerParams) {

    override val workName: String = ADD_CATEGORY_TAG
    override val notificationMsg: String = applicationContext.getString(R.string.category_adding)
    override val notificationId: Int = ADD_CATEGORY_NOTIFICATION_ID

    companion object {
        const val ADD_CATEGORY_TAG = "Category adding"

        fun createWorkRequest(addingCategory: DomainCategory): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<AddCategoryWorker>()
                .addTag(ADD_CATEGORY_TAG)
                .setInputData(WorkUtils.serialize(addingCategory))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }

    override suspend fun calculate(inputArg: DomainCategory): kotlin.Result<Long> {
        delay(1000)
        return addCategory(inputArg).map { it.first() }
    }
}
