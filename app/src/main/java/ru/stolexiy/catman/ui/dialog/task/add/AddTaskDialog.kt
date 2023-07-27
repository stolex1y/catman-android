package ru.stolexiy.catman.ui.dialog.task.add

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.DialogTaskAddBinding
import ru.stolexiy.catman.ui.common.getJson
import ru.stolexiy.catman.ui.common.removeJson
import ru.stolexiy.catman.ui.common.setJson
import ru.stolexiy.catman.ui.dialog.AbstractBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.common.listadapter.CategoryNameWithColorAdapter
import ru.stolexiy.catman.ui.dialog.common.model.Category
import ru.stolexiy.catman.ui.dialog.custom.DatePicker
import ru.stolexiy.catman.ui.dialog.custom.TimePicker
import ru.stolexiy.catman.ui.dialog.task.TaskSettingsDialogEntryPoint
import ru.stolexiy.catman.ui.dialog.task.TaskSettingsDialogEvent
import ru.stolexiy.catman.ui.dialog.task.TaskSettingsViewModel
import ru.stolexiy.catman.ui.dialog.task.model.Purpose
import ru.stolexiy.catman.ui.dialog.task.model.PurposeNameWithColorAdapter
import ru.stolexiy.catman.ui.dialog.task.model.Task
import ru.stolexiy.catman.ui.util.binding.BindingDelegate.Companion.bindingDelegate
import ru.stolexiy.catman.ui.util.di.entryPointAccessors
import ru.stolexiy.catman.ui.util.fragment.repeatOnParentViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.repeatOnViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.requireParentView
import ru.stolexiy.catman.ui.util.validation.Condition.Companion.isValid
import ru.stolexiy.catman.ui.util.viewmodel.CustomAbstractSavedStateViewModelFactory.Companion.assistedViewModels
import ru.stolexiy.common.DateUtils.toLocalTime
import ru.stolexiy.common.DateUtils.toTime
import ru.stolexiy.common.DateUtils.toZonedDateTime
import ru.stolexiy.common.DateUtils.updateDate
import ru.stolexiy.common.DateUtils.updateTime
import ru.stolexiy.common.Json
import ru.stolexiy.common.timer.Time
import java.time.LocalDate
import java.time.ZoneOffset

abstract class AddTaskDialog(
    @LayoutRes layout: Int,
    onDestroyDialog: () -> Unit = {}
) : AbstractBottomDialogFragment(layout, onDestroyDialog) {

    protected lateinit var task: Task

    private val entryPointProvider: TaskSettingsDialogEntryPoint by entryPointAccessors()

    private val viewModel: TaskSettingsViewModel by assistedViewModels<TaskSettingsViewModel> {
        entryPointProvider.taskSettingsViewModelFactory().create(it, null)
    }

    private val binding: DialogTaskAddBinding by bindingDelegate()

    private val deadlineDateDialog: DatePicker by lazy {
        DatePicker(
            R.string.deadline,
            selection = task.deadline.get()?.toLocalDate() ?: LocalDate.now(),
            validator = { task.deadline.condition.isValid(it.toZonedDateTime(ZoneOffset.UTC)) },
            onPositiveButtonClickListener = {
                task.deadline.set(task.deadline.get().updateDate(it))
            }
        )
    }

    private val deadlineTimeDialog: TimePicker by lazy {
        TimePicker(
            R.string.deadline,
            task.deadline.get()?.toTime() ?: Time.ZERO
        ) {
            task.deadline.set(task.deadline.get().updateTime(it.toLocalTime()))
        }
    }

    private val startDateDialog: DatePicker by lazy {
        DatePicker(
            R.string.start_time,
            selection = task.startTime.get()?.toLocalDate() ?: LocalDate.now(),
            validator = { task.startTime.condition.isValid(it.toZonedDateTime(ZoneOffset.UTC)) },
            onPositiveButtonClickListener = {
                task.startTime.set(task.startTime.get().updateDate(it))
            }
        )
    }

    private val startTimeDialog: TimePicker by lazy {
        TimePicker(
            R.string.start_time,
            task.startTime.get()?.toTime() ?: Time.ZERO
        ) {
            task.startTime.set(task.startTime.get().updateTime(it.toLocalTime()))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dispatchEvent(
            TaskSettingsDialogEvent.Load
        )
        restoreState()
        binding.apply {
            task = task
            lifecycleOwner = viewLifecycleOwner
            taskDeadlineDate.setOnClickListener {
                deadlineDateDialog.show(
                    childFragmentManager,
                    DATETIME_PICKER
                )
            }
            taskDeadlineTime.setOnClickListener {
                deadlineTimeDialog.show(
                    childFragmentManager,
                    DATETIME_PICKER
                )
            }
            taskStartDate.setOnClickListener {
                startDateDialog.show(
                    childFragmentManager,
                    DATETIME_PICKER
                )
            }
            taskStartTime.setOnClickListener {
                startTimeDialog.show(
                    childFragmentManager,
                    DATETIME_PICKER
                )
            }
            purposeCategory.onItemClickListener = onCategoryClickListener()
            taskPurpose.onItemClickListener = onPurposeClickListener()
            addTaskButton.setOnClickListener { addTask() }
        }
        observeData()
        observeStateWithParentView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        saveState()
    }

    private fun restoreState() {
        task = findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getJson(Json.serializer, ADDING_TASK)
            ?: Task()
    }

    private fun saveState() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.setJson(Json.serializer, ADDING_TASK, task)
    }

    private fun clearState() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.removeJson(ADDING_TASK)
    }

    private fun observeStateWithParentView() {
        val parentView = requireParentView()
        repeatOnParentViewLifecycle {
            viewModel.state.collect { newState ->
                when (newState) {
                    TaskSettingsViewModel.State.Adding -> showAddingPurposeSnackbar(parentView)

                    TaskSettingsViewModel.State.Added -> showAddedPurposeSnackbar(parentView)

                    TaskSettingsViewModel.State.Deleting -> showCancellingSnackbar(parentView)

                    TaskSettingsViewModel.State.Deleted -> showCancelledSnackbar(parentView)

                    TaskSettingsViewModel.State.Canceled -> showCancelledSnackbar(parentView)

                    is TaskSettingsViewModel.State.Error -> {
                        showErrorSnackbar(
                            viewModel.prevState,
                            newState.error,
                            parentView
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showAddingPurposeSnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(TaskSettingsDialogEvent.Cancel) }
            setText(R.string.task_adding)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showAddedPurposeSnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(TaskSettingsDialogEvent.DeleteAdded) }
            setText(R.string.task_added)
            duration = Snackbar.LENGTH_SHORT
        }
    }

    private fun showCancellingSnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setText(R.string.cancelling)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showCancelledSnackbar(parenView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parenView) {
            setText(R.string.cancelled)
            duration = Snackbar.LENGTH_SHORT
        }
    }

    private fun observeData() {
        repeatOnViewLifecycle {
            viewModel.data.collectLatest { data ->
                val categories = data.categoriesWithPurposes.keys.toList()
                setCategoryList(categories)

                val currentCategory: Category? =
                    task.purpose.get()?.category ?: categories.firstOrNull()
                val currentPurposes: List<Purpose> = if (currentCategory == null)
                    emptyList()
                else
                    data.categoriesWithPurposes.getOrDefault(currentCategory, emptyList())
                setPurposeList(currentPurposes)
            }
        }
    }

    private fun addTask() {
        viewModel.dispatchEvent(TaskSettingsDialogEvent.Add(task))
        clearState()
        dismissNow()
    }

    private fun onCategoryClickListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selection = parent!!.getItemAtPosition(position) as Category
            task.category.set(selection)
            setPurposeList(getPurposesByCategory(selection))
        }

    private fun onPurposeClickListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selection = parent!!.getItemAtPosition(position) as Purpose
            task.purpose.set(selection)
        }


    private fun setCategoryList(categories: List<Category>) {
        val currentCategory: Category? =
            task.purpose.get()?.category ?: categories.firstOrNull()
        binding.purposeCategory.setAdapter(
            CategoryNameWithColorAdapter(
                categories,
                requireContext()
            )
        )
        if (currentCategory != null) {
            val selectedIdx = categories.indexOf(currentCategory)
            binding.purposeCategory.listSelection = selectedIdx
        }
    }

    private fun setPurposeList(purposes: List<Purpose>) {
        binding.taskPurpose.setAdapter(
            PurposeNameWithColorAdapter(
                purposes,
                requireContext()
            )
        )
        if (task.purpose.get() != null) {
            val selectedIdx = purposes.indexOf(task.purpose.get())
            binding.taskPurpose.listSelection = selectedIdx
        }
    }

    private fun getPurposesByCategory(category: Category?): List<Purpose> {
        return if (category == null)
            emptyList()
        else
            viewModel.data.value.categoriesWithPurposes.getOrDefault(category, emptyList())
    }

    private fun showErrorSnackbar(
        operation: TaskSettingsViewModel.State,
        @StringRes errorText: Int,
        parentView: View
    ) {
        val failedOperation = when (operation) {
            TaskSettingsViewModel.State.Adding -> R.string.task_failed_adding
            TaskSettingsViewModel.State.Deleting -> R.string.task_failed_deleting
            else -> R.string.internal_error
        }
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setText(failedOperation)
            duration = Snackbar.LENGTH_SHORT
        }
        entryPointProvider.snackbarManager().addSnackbar(parentView) {
            setText(errorText)
            duration = Snackbar.LENGTH_LONG
        }
    }

    private companion object {
        const val ADDING_TASK = "ADDING_TASK"
        const val DATETIME_PICKER = "DATETIME_PICKER"
    }
}
