package ru.stolexiy.catman.ui.dialog.purpose.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
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

class AddPurposeViewModel(
    private val mApplication: CatmanApplication,
    private val mCategoryCrud: CategoryCrud,
) : ViewModel() {

    private val mState: MutableStateFlow<State> = MutableStateFlow(State.Init)
    val state: StateFlow<State> = mState.asStateFlow()

    init {
        initState()
    }

    fun addPurpose(purpose: Purpose) {
        if (!purpose.isValid)
            throw IllegalStateException()
        OneTimeWorkRequestBuilder<AddPurposeWorker>()
            .setInputData(workDataOf(AddPurposeWorker.INPUT_PURPOSE to purpose.toDomainPurpose()))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
            .run(WorkManager.getInstance(mApplication)::enqueue)
    }

    private fun initState() {
        viewModelScope.launch {
            coroutineScope {
                getCategories().collectLatest { categories ->
                    mState.value = State.Loaded(categories)
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
        data class Loaded(val categories: List<Category>) : State()
        data class Error(val error: String) : State()
    }
}