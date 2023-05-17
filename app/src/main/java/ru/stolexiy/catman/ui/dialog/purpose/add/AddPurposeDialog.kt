package ru.stolexiy.catman.ui.dialog.purpose.add

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.DialogPurposeAddBinding
import ru.stolexiy.catman.domain.util.DateUtils.toCalendar
import ru.stolexiy.catman.ui.dialog.AbstractBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.adapter.TextWithColorAdapter
import ru.stolexiy.catman.ui.dialog.custom.DatePicker
import ru.stolexiy.catman.ui.dialog.purpose.add.di.AddPurposeDialogEntryPoint
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.util.binding.BindingDelegate
import ru.stolexiy.catman.ui.util.notification.showWorkInfoSnackbar
import ru.stolexiy.catman.ui.util.state.ActionInfo
import ru.stolexiy.catman.ui.util.viewmodel.CustomAbstractSavedStateViewModelFactory.Companion.assistedViewModels
import timber.log.Timber

class AddPurposeDialog(
    onDestroyDialog: () -> Unit = {}
) : AbstractBottomDialogFragment(R.layout.dialog_purpose_add, onDestroyDialog) {

    private lateinit var addingPurpose: Purpose
    private lateinit var assistedViewModelFactory: AddPurposeViewModel.Factory

    private val viewModel: AddPurposeViewModel by assistedViewModels<AddPurposeViewModel> {
        assistedViewModelFactory.create(it)
    }

    private val binding: DialogPurposeAddBinding by BindingDelegate(
        view,
        viewLifecycleOwner.lifecycle
    )

    private val deadlineDialog: DatePicker by lazy {
        initChooseDeadlineDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dependencies =
            EntryPointAccessors.fromFragment(this, AddPurposeDialogEntryPoint::class.java)
        assistedViewModelFactory = dependencies.assistedViewModelFactory()
        restoreState()
        binding.apply {
            purpose = addingPurpose
            lifecycleOwner = viewLifecycleOwner
            purposeDeadlineLayout.setStartIconOnClickListener { chooseDeadline() }
            purposeDeadline.setOnClickListener { chooseDeadline() }
            purposeCategory.onItemClickListener = onCategoryClickListener()
            addPurposeButton.setOnClickListener { addPurpose() }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.state.collect { handleState(it) }
                }
                launch {
                    viewModel.categories.collectLatest { onUpdateCategories(it) }
                }
            }
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

    private fun handleState(newState: ActionInfo) {
        when (newState) {
            is ActionInfo.Error -> {
                Toast.makeText(requireContext(), "Error...", Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    private fun onUpdateCategories(categories: List<Category>) {
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

    private fun addPurpose() {
        if (addingPurpose.isValid) {
            viewModel.addPurpose(addingPurpose).run(this::onAdding)
            clearState()
            dismiss()
        }
    }

    private fun onAdding(workInfo: Flow<WorkInfo>) {
        parentFragment?.viewLifecycleOwner?.lifecycleScope?.launch {
            requireParentFragment().viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val view = requireParentFragment().view ?: return@repeatOnLifecycle
                view
                viewModel.state.collectLatest {
                    when (it) {
                        is AddPurposeViewModel.State.Adding ->
                        is AddPurposeViewModel.State.Deleting ->
                        is AddPurposeViewModel.State.Error ->
                        is AddPurposeViewModel.State.Loaded ->
                    }
                }
                requireParentFragment().view?.run {
                    showWorkInfoSnackbar(
                        view = this,
                        appContext = requireActivity().applicationContext,
                        workInfo = workInfo,
                        runningMsg = R.string.purpose_adding,
                        successMsg = R.string.purpose_added,
                        onCancelResult = this@AddPurposeDialog::onAddingCancel
                    )
                }
            }
        }
    }

    private fun onAddingCancel(addingWorkInfo: WorkInfo) {
        val deletingWorkInfo = viewModel.revertAdding(addingWorkInfo)
        parentFragment?.viewLifecycleOwner?.lifecycleScope?.launchWhenStarted {
            requireParentFragment().view?.run {
                Timber.d("on deleting")
                showWorkInfoSnackbar(
                    view = this,
                    appContext = requireActivity().applicationContext,
                    workInfo = deletingWorkInfo,
                    runningMsg = R.string.purpose_deleting,
                    successMsg = R.string.purpose_deleted
                )
            }
        }
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
