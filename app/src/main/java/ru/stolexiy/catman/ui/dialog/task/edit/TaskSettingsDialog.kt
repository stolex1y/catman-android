/*
package ru.stolexiy.catman.ui.dialog.task.edit

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.DialogPurposeAddBinding
import ru.stolexiy.catman.ui.dialog.AbstractBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.custom.DatePicker
import ru.stolexiy.catman.ui.dialog.purpose.add.AddPurposeEvent
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.CategoryNameWithColorAdapter
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.task.TaskSettingsDialogEntryPoint
import ru.stolexiy.catman.ui.dialog.task.TaskSettingsViewModel
import ru.stolexiy.catman.ui.dialog.task.model.Task
import ru.stolexiy.catman.ui.util.binding.BindingDelegate.Companion.bindingDelegate
import ru.stolexiy.catman.ui.util.di.entryPointAccessors
import ru.stolexiy.catman.ui.util.fragment.repeatOnParentViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.repeatOnViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.requireParentView
import ru.stolexiy.catman.ui.util.viewmodel.CustomAbstractSavedStateViewModelFactory.Companion.assistedViewModels
import ru.stolexiy.common.DateUtils.toZonedDateTime
import timber.log.Timber
import java.time.ZoneOffset
import java.time.ZonedDateTime

abstract class TaskSettingsDialog(
    @LayoutRes layout: Int,
    onDestroyDialog: () -> Unit = {}
) : AbstractBottomDialogFragment(layout, onDestroyDialog) {

    protected lateinit var task: Task

    private val entryPointProvider: TaskSettingsDialogEntryPoint by entryPointAccessors()

    private val viewModel: TaskSettingsViewModel by assistedViewModels<TaskSettingsViewModel> {
        entryPointProvider.taskSettingsViewModelFactory().create(it, na)
    }

    private val binding: DialogPurposeAddBinding by bindingDelegate()

    private val deadlineDialog: DatePicker by lazy {
        initChooseDeadlineDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dispatchEvent(
            AddPurposeEvent.Load)
        restoreState()
        binding.apply {
            purpose = task
            lifecycleOwner = viewLifecycleOwner
            purposeDeadlineLayout.setStartIconOnClickListener { chooseDeadline() }
            purposeDeadline.setOnClickListener { chooseDeadline() }
            purposeCategory.onItemClickListener = onCategoryClickListener()
            addPurposeButton.setOnClickListener { addPurpose() }
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
            ?.get<Purpose>(ADDING_PURPOSE)
            ?: Purpose()
    }

    private fun saveState() {
        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            it[ADDING_PURPOSE] = task
        }
    }

    private fun clearState() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.remove<Purpose>(ADDING_PURPOSE)
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

                    else -> {}
                }
            }
        }
    }

    private fun showAddingPurposeSnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(AddPurposeEvent.Cancel) }
            setText(R.string.purpose_adding)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showAddedPurposeSnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(AddPurposeEvent.DeleteAdded) }
            setText(R.string.purpose_added)
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
                val categories = data.categories
                binding.purposeCategory.setAdapter(
                    CategoryNameWithColorAdapter(categories, requireContext())
                )
                if (categories.isNotEmpty()) {
                    val selected = task.category.get() ?: categories.first()
                    val selectedIdx = categories.indexOf(selected).takeIf { it >= 0 } ?: 0
                    binding.purposeCategory.listSelection = selectedIdx
                }
            }
        }
    }

    private fun addPurpose() {
        viewModel.dispatchEvent(AddPurposeEvent.Add(task))
        clearState()
        dismissNow()
    }

    private fun initChooseDeadlineDialog(): DatePicker {
        val datePicker = DatePicker(
            R.string.deadline,
            selection = task.deadline.get() ?: ZonedDateTime.now(),
            condition = task.deadline.condition
        )
        datePicker.dialog.addOnPositiveButtonClickListener {
            task.deadline.set(it.toZonedDateTime(ZoneOffset.UTC))
        }
        return datePicker
    }

    private fun chooseDeadline() {
        if (deadlineDialog.dialog.isResumed)
            return
        Timber.d("choose deadline")
        deadlineDialog.dialog.show(childFragmentManager, "CHOOSE_DEADLINE")
    }

    private fun onCategoryClickListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selection = parent!!.getItemAtPosition(position) as Category
            task.category.set(selection)
            binding.purposeCategory.setText(selection.name)
        }

    private companion object {
        const val ADDING_PURPOSE = "ADDING_PURPOSE"
    }
}
*/
