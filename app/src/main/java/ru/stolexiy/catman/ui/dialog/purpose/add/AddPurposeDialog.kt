package ru.stolexiy.catman.ui.dialog.purpose.add

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.DialogPurposeAddBinding
import ru.stolexiy.catman.ui.dialog.AbstractBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.common.listadapter.CategoryNameWithColorAdapter
import ru.stolexiy.catman.ui.dialog.common.model.Category
import ru.stolexiy.catman.ui.dialog.custom.DatePicker
import ru.stolexiy.catman.ui.dialog.purpose.add.di.AddPurposeDialogEntryPoint
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.util.binding.BindingDelegate.Companion.bindingDelegate
import ru.stolexiy.catman.ui.util.di.entryPointAccessors
import ru.stolexiy.catman.ui.util.fragment.repeatOnParentViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.repeatOnViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.requireParentView
import ru.stolexiy.catman.ui.util.viewmodel.CustomAbstractSavedStateViewModelFactory.Companion.assistedViewModels
import timber.log.Timber
import java.time.LocalDate

class AddPurposeDialog(
    onDestroyDialog: () -> Unit = {}
) : AbstractBottomDialogFragment(R.layout.dialog_purpose_add, onDestroyDialog) {

    private lateinit var addingPurpose: Purpose

    private val entryPointProvider: AddPurposeDialogEntryPoint by entryPointAccessors()

    private val viewModel: AddPurposeViewModel by assistedViewModels<AddPurposeViewModel> {
        entryPointProvider.addPurposeViewModelFactory().create(it)
    }

    private val binding: DialogPurposeAddBinding by bindingDelegate()

    private val deadlineDialog: DatePicker by lazy {
        DatePicker(
            R.string.deadline,
            selection = addingPurpose.deadline.get() ?: LocalDate.now(),
            condition = addingPurpose.deadline.condition,
            onPositiveButtonClickListener = { addingPurpose.deadline.set(it) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dispatchEvent(AddPurposeDialogEvent.Load)
        restoreState()
        binding.apply {
            purpose = addingPurpose
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
        addingPurpose = findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.get<Purpose>(ADDING_PURPOSE)
            ?: Purpose()
    }

    private fun saveState() {
        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            it[ADDING_PURPOSE] = addingPurpose
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
                    AddPurposeViewModel.State.Adding -> showAddingPurposeSnackbar(parentView)

                    AddPurposeViewModel.State.Added -> showAddedPurposeSnackbar(parentView)

                    AddPurposeViewModel.State.Deleting -> showCancellingSnackbar(parentView)

                    AddPurposeViewModel.State.Deleted -> showCancelledSnackbar(parentView)

                    AddPurposeViewModel.State.Canceled -> showCancelledSnackbar(parentView)

                    is AddPurposeViewModel.State.Error -> {
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
            setAction(R.string.cancel) { viewModel.dispatchEvent(AddPurposeDialogEvent.Cancel) }
            setText(R.string.purpose_adding)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showAddedPurposeSnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(AddPurposeDialogEvent.DeleteAdded) }
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

    private fun showCancelledSnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setText(R.string.cancelled)
            duration = Snackbar.LENGTH_SHORT
        }
    }

    private fun showErrorSnackbar(
        operation: AddPurposeViewModel.State,
        @StringRes errorText: Int,
        parentView: View
    ) {
        val failedOperation = when (operation) {
            AddPurposeViewModel.State.Adding -> R.string.purpose_failed_adding
            AddPurposeViewModel.State.Deleting -> R.string.purpose_failed_deleting
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

    private fun observeData() {
        repeatOnViewLifecycle {
            viewModel.data.collectLatest { data ->
                val categories = data.categories
                binding.purposeCategory.setAdapter(
                    CategoryNameWithColorAdapter(categories, requireContext())
                )
                if (categories.isNotEmpty()) {
                    val selected = addingPurpose.category.get() ?: categories.first()
                    val selectedIdx = categories.indexOf(selected).takeIf { it >= 0 } ?: 0
                    binding.purposeCategory.listSelection = selectedIdx
                }
            }
        }
    }

    private fun addPurpose() {
        viewModel.dispatchEvent(AddPurposeDialogEvent.Add(addingPurpose))
        clearState()
        dismissNow()
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
            addingPurpose.category.set(selection)
//            binding.purposeCategory.setText(selection.name)
        }

    private companion object {
        const val ADDING_PURPOSE = "ADDING_PURPOSE"
    }
}
