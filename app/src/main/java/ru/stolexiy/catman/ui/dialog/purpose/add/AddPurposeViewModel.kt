package ru.stolexiy.catman.ui.dialog.purpose.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
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
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.purpose.model.toCategory
import ru.stolexiy.catman.ui.util.state.ActionInfo
import ru.stolexiy.catman.ui.util.udf.SimpleLoadingState
import ru.stolexiy.catman.ui.util.udf.UdfViewModel
import ru.stolexiy.catman.ui.util.work.purpose.AddPurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker
import timber.log.Timber

class AddPurposeViewModel(
    private val mApplication: CatmanApplication,
    private val mCategoryCrud: CategoryCrud,
    savedStateHandle: SavedStateHandle
) : UdfViewModel<AddPurposeAction, SimpleLoadingState>(savedStateHandle) {

    private val mCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = mCategories.asStateFlow()

    init {
        initState()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("cleared")
    }

    fun addPurpose(purpose: Purpose): Flow<WorkInfo> {
        if (!purpose.isValid)
            throw IllegalArgumentException()

        val worker = AddPurposeWorker.create(purpose.toDomainPurpose())
        return WorkManager.getInstance(mApplication).run {
            enqueue(worker)
            getWorkInfoByIdLiveData(worker.id).asFlow().apply { this.observeWorkInfo() }
        }
    }

    fun revertAdding(addingWorkInfo: WorkInfo) {
        if (!addingWorkInfo.tags.contains(AddPurposeWorker.ADD_PURPOSE_TAG))
            throw IllegalStateException()
        if (!addingWorkInfo.state.isFinished)
            throw IllegalStateException()
        if (!addingWorkInfo.outputData.keyValueMap.containsKey(AddPurposeWorker.OUTPUT_PURPOSE_ID))
            throw IllegalStateException()
        val deletingId =
            addingWorkInfo.outputData.keyValueMap[AddPurposeWorker.OUTPUT_PURPOSE_ID] as Long
        deleteAddedPurpose(deletingId).apply { this.observeWorkInfo() }
    }

    private fun deleteAddedPurpose(deletingId: Long): Flow<WorkInfo> {
        val worker = DeletePurposeWorker.create(deletingId)
        return WorkManager.getInstance(mApplication).run {
            enqueue(worker)
            getWorkInfoByIdLiveData(worker.id).asFlow()
        }
    }

    private fun Flow<WorkInfo>.observeWorkInfo() {
        viewModelScope.launch {
            this@observeWorkInfo.collectLatest {
                if (it.state.isFinished) {
                    mState.value = when (it.state) {
                        WorkInfo.State.FAILED -> ActionInfo.Error(mApplication.getString(R.string.internal_error))
                        else -> ActionInfo.Loaded
                    }
                    cancel()
                } else if (mState.value !is ActionInfo.Loading) {
                    mState.value = ActionInfo.Loading(this@observeWorkInfo)
                }
            }
        }
    }

    private fun initState() {
        viewModelScope.launch {
            coroutineScope {
                getCategories().first().let {
                    mCategories.value = it
                    mState.value = ActionInfo.Loaded
                }
                getCategories().collectLatest { categories ->
                    mCategories.value = categories
                }
            }
        }
    }

    private fun getCategories() =
        mCategoryCrud.getAll()
            .onEach(this::handleError)
            .mapNotNull {
                it.getOrNull()?.map(DomainCategory::toCategory)
            }

    private fun <T> handleError(result: Result<T>) {
        if (result.isFailure) {
            mState.value = ActionInfo.Error(mApplication.getString(R.string.internal_error))
            throw CancellationException()
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CatmanApplication
                val savedStateHandle = extras.createSavedStateHandle()
                return AddPurposeViewModel(
                    application,
                    application.categoryCrud,
                    savedStateHandle
                ) as T
            }
        }
    }
}