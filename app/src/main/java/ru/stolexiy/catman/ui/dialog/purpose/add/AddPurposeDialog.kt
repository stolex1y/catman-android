package ru.stolexiy.catman.ui.dialog.purpose.add

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.DialogPurposeAddBinding
import ru.stolexiy.common.DateUtils.toCalendar
import ru.stolexiy.catman.ui.dialog.AbstractBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.adapter.TextWithColorAdapter
import ru.stolexiy.catman.ui.dialog.custom.DatePicker
import ru.stolexiy.catman.ui.dialog.purpose.add.di.AddPurposeDialogEntryPoint
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.util.binding.BindingDelegate.Companion.bindingDelegate
import ru.stolexiy.catman.ui.util.di.entryPointAccessors
import ru.stolexiy.catman.ui.util.fragment.repeatOnViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.requireParentView
import ru.stolexiy.catman.ui.util.viewmodel.CustomAbstractSavedStateViewModelFactory.Companion.assistedViewModels
import timber.log.Timber

class AddPurposeDialog(
    onDestroyDialog: () -> Unit = {}
) : AbstractBottomDialogFragment(R.layout.dialog_purpose_add, onDestroyDialog) {

    private lateinit var addingPurpose: Purpose

    private val entryPointProvider: AddPurposeDialogEntryPoint by entryPointAccessors()

    private val viewModel: AddPurposeViewModel by assistedViewModels<AddPurposeViewModel> {
        entryPointProvider.assistedViewModelFactory().create(it)
    }

    private val binding: DialogPurposeAddBinding by bindingDelegate()

    private val deadlineDialog: DatePicker by lazy {
        initChooseDeadlineDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreState()
        binding.apply {
            purpose = addingPurpose
            lifecycleOwner = viewLifecycleOwner
            purposeDeadlineLayout.setStartIconOnClickListener { chooseDeadline() }
            purposeDeadline.setOnClickListener { chooseDeadline() }
            purposeCategory.onItemClickListener = onCategoryClickListener()
//            purposeCategory.onItemSelectedListener = onSelectCategoryListener()
            addPurposeButton.setOnClickListener { addPurpose() }
        }
        repeatOnViewLifecycle {
            viewModel.state.collect { onNewState(it) }
        }
        repeatOnViewLifecycle {
            viewModel.data.collectLatest { onNewData(it) }
        }
        requireParentFragment().viewLifecycleOwner.lifecycleScope.launch {
            val parentView = requireParentView()
            viewModel.state.collect { updateSnackbarWithNewState(parentView, it) }
        }
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

    private fun onNewState(newState: AddPurposeViewModel.State) {
        when (newState) {
            AddPurposeViewModel.State.Init -> {
                Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
            }

            AddPurposeViewModel.State.Loaded -> {
                Toast.makeText(requireContext(), "Loaded...", Toast.LENGTH_SHORT).show()
            }

            is AddPurposeViewModel.State.Error -> {
                Toast.makeText(
                    requireContext(),
                    "Error... ${getString(newState.error)}",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }
    }

    private fun onNewData(data: AddPurposeViewModel.Data) {
        val categories = data.categories
        binding.purposeCategory.setAdapter(
            TextWithColorAdapter(categories, requireContext()) {
                TextWithColorAdapter.Item(it.id, it.color, it.name)
            }
        )
        if (categories.isNotEmpty()) {
            val selected = addingPurpose.category.get() ?: categories.first()
            val selectedIdx = categories.indexOf(selected).takeIf { it >= 0 } ?: 0
            binding.purposeCategory.listSelection = selectedIdx
        }
    }

    private fun updateSnackbarWithNewState(
        parentView: View,
        newState: AddPurposeViewModel.State
    ) {
        when (newState) {
            AddPurposeViewModel.State.Adding -> {
                entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
                    setText(R.string.purpose_adding)
                    setAction(R.string.cancel) {
                        viewModel.dispatchEvent(AddPurposeEvent.Cancel)
                    }
                    duration = Snackbar.LENGTH_INDEFINITE
                }
            }

            AddPurposeViewModel.State.Added -> {
                entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
                    setText(R.string.purpose_added)
                    setAction(R.string.cancel) {
                        viewModel.dispatchEvent(AddPurposeEvent.DeleteAdded)
                    }
                    duration = Snackbar.LENGTH_SHORT
                }
            }

            AddPurposeViewModel.State.Deleting -> {
                entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
                    setText(R.string.purpose_deleting)
                    setAction(R.string.cancel) {
                        viewModel.dispatchEvent(AddPurposeEvent.Cancel)
                    }
                    duration = Snackbar.LENGTH_INDEFINITE
                }
            }

            AddPurposeViewModel.State.Deleted -> {
                entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
                    setText(R.string.purpose_deleted)
                    duration = Snackbar.LENGTH_SHORT
                }
            }

            else -> {}
        }
    }

    private fun addPurpose() {
        viewModel.dispatchEvent(AddPurposeEvent.Add(addingPurpose))
        clearState()
        dismissNow()
    }

    private fun initChooseDeadlineDialog(): DatePicker {
        val datePicker = DatePicker(
            R.string.deadline,
            selection = addingPurpose.deadline.get()?.timeInMillis ?: System.currentTimeMillis(),
            condition = addingPurpose.deadline.condition
        )
        datePicker.dialog.addOnPositiveButtonClickListener {
            addingPurpose.deadline.set(it.toCalendar())
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
            val selection =
                (parent!!.getItemAtPosition(position) as TextWithColorAdapter.Item).toCategory()
            addingPurpose.category.set(selection)
            binding.purposeCategory.setText(selection.name)
        }

    private companion object {
        const val ADDING_PURPOSE = "ADDING_PURPOSE"
    }
}
