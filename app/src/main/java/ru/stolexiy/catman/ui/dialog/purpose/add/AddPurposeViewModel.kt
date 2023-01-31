package ru.stolexiy.catman.ui.dialog.purpose.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.work.Data
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
import ru.stolexiy.catman.ui.util.work.purpose.AddPurposeWorker
import ru.stolexiy.catman.ui.util.work.purpose.DeletePurposeWorker
import timber.log.Timber

class AddPurposeViewModel(
    private val mApplication: CatmanApplication,
    private val mCategoryCrud: CategoryCrud,
) : ViewModel() {

    private val mState: MutableStateFlow<State> = MutableStateFlow(State.Init)
    val state: StateFlow<State> = mState.asStateFlow()

    private val mCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = mCategories.asStateFlow()

    init {
        initState()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("cleared")
    }

    fun addPurpose(purpose: Purpose) {
        if (!purpose.isValid)
            throw IllegalArgumentException()

        val worker = AddPurposeWorker.create(purpose.toDomainPurpose())
        WorkManager.getInstance(mApplication).run {
            enqueue(worker)
            getWorkInfoByIdLiveData(worker.id).asFlow()
        }.let { workInfo ->
            mState.value = State.Adding(workInfo)
            observeWorkInfo(workInfo)
        }
    }

    fun revertAdding(workInfo: WorkInfo): Flow<WorkInfo>? {
        if (!workInfo.tags.contains(AddPurposeWorker.ADD_PURPOSE_TAG))
            throw IllegalStateException()
        if (!workInfo.state.isFinished)
            throw IllegalStateException()

        /* // if source 'workInfo' worker is not finished
        val workManager = WorkManager.getInstance(mApplication)
        if (!workInfo.state.isFinished) {
            viewModelScope.launch {
                workManager.getWorkInfoByIdLiveData(workInfo.id).asFlow().collectLatest {
                    if (it.state.isFinished) {
                        if (it.state == WorkInfo.State.SUCCEEDED)
                            deleteAddedPurpose(it.outputData)
                        cancel()
                    }
                }
            }
        } else {*/
        return deleteAddedPurpose(workInfo.outputData)
    }

    private fun deleteAddedPurpose(outputAddPurposeWorker: Data): Flow<WorkInfo>? {
        val deletingPurposeId =
            outputAddPurposeWorker.keyValueMap[AddPurposeWorker.OUTPUT_PURPOSE_ID] as Long?
        return if (deletingPurposeId != null) {
            val worker = DeletePurposeWorker.create(deletingPurposeId)
            WorkManager.getInstance(mApplication).run {
                enqueue(worker)
                getWorkInfoByIdLiveData(worker.id).asFlow()
            }
        } else
            null
    }

    private fun observeWorkInfo(workInfo: Flow<WorkInfo>) {
        viewModelScope.launch {
            workInfo.collectLatest {
                if (it.state.isFinished) {
                    mState.value = State.Loaded
                    cancel()
                }
            }
        }
    }

    private fun initState() {
        viewModelScope.launch {
            coroutineScope {
                getCategories().collectLatest { categories ->
                    mCategories.value = categories
                    mState.value = State.Loaded
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
            mState.value = State.Error(mApplication.getString(R.string.internal_error))
            throw CancellationException()
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CatmanApplication
                return AddPurposeViewModel(
                    application,
                    application.categoryCrud,
                ) as T
            }
        }
    }

    sealed class State {
        object Init : State()
        object Loaded : State()
        data class Error(val error: String) : State()
        data class Adding(val workState: Flow<WorkInfo>) : State()
    }
}