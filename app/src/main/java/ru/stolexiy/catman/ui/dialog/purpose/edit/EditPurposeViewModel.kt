package ru.stolexiy.catman.ui.dialog.purpose.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation.State.FAILURE
import androidx.work.Operation.State.SUCCESS
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.usecase.CategoryCrud
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.purpose.model.toCategory
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.UpdatePurposeWorker

class EditPurposeViewModel(
    private val editingPurposeId: Long,
    private val purposeCrud: PurposeCrud,
    private val categoryCrud: CategoryCrud,
    private val application: CatmanApplication
) : ViewModel() {

    private val mState: MutableStateFlow<State> = MutableStateFlow(State.Init)
    val state: StateFlow<State> = mState.asStateFlow()

    private lateinit var mUpdatingPurpose: Purpose
    private val mCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = mCategories.asStateFlow()

    init {
        initState()
    }

    fun updatePurpose(purpose: Purpose): Flow<WorkInfo> {
        if (!purpose.isValid)
            throw IllegalStateException()
        mState.value = State.Loading
        val worker = UpdatePurposeWorker.create(purpose.toDomainPurpose())
        val workStateFlow = OneTimeWorkRequestBuilder<UpdatePurposeWorker>()
            .setInputData(workDataOf(UpdatePurposeWorker.INPUT_PURPOSE to updatingPurpose))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
            .run(WorkManager.getInstance(application)::enqueue)
            .state.asFlow()
    }

    fun deletePurpose() {
        if (mState.value is State.Deleted)
            return
        mState.value = State.Loading
        val workStateFlow = OneTimeWorkRequestBuilder<DeletePurposeWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
            .run(WorkManager.getInstance(application)::enqueue)
            .state.asFlow()

        viewModelScope.launch(SupervisorJob()) {
            workStateFlow.collect {
                when (it) {
                    is SUCCESS -> {
                        mState.value = State.Deleted
                        cancel()
                    }
                    is FAILURE -> {
                        mState.value = State.Error(application.getString(R.string.internal_error))
                        cancel()
                    }
                }
            }
        }
    }

    private fun initState() {
        viewModelScope.launch {
            coroutineScope {
                val categoriesJob = async { getCategories().first() }
                val purposeResult = getDomainPurpose(editingPurposeId).first()
                mCategories.value = categoriesJob.await()
                mUpdatingPurpose = Purpose.fromDomainPurpose(purposeResult, mCategories.value.findLast { it.id == purposeResult.categoryId }!!)
                mState.value = State.Loaded(mUpdatingPurpose)
                observeCategories()
            }
        }
    }

    private suspend fun observeCategories() =
        getCategories().collectLatest { mCategories.value = it }

    private fun getCategories() =
        categoryCrud.getAll()
            .onEach(this::handleError)
            .mapNotNull {
                it.getOrNull()?.map(DomainCategory::toCategory)
            }

    private fun getDomainPurpose(purposeId: Long) =
        purposeCrud.get(purposeId)
            .onEach(this::handleError)
            .mapNotNull { it.getOrNull() }

    private fun <T> handleError(result: Result<T>) {
        if (result.isFailure) {
            mState.value = State.Error(application.getString(R.string.internal_error))
            throw CancellationException()
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun createFactory(purposeId: Long): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CatmanApplication
                return EditPurposeViewModel(
                    purposeId,
                    application.purposeCrud,
                    application.categoryCrud,
                    application
                ) as T
            }
        }
    }

    sealed class State {
        object Init : State()
        object Loading : State()
        class Error(val error: String) : State()
        data class Loaded(val updatingPurpose: Purpose) : State()
        object Deleted : State()
    }
}
