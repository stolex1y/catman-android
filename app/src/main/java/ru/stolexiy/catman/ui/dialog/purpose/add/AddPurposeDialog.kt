package ru.stolexiy.catman.ui.dialog.purpose.add

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.DateUtils.toCalendar
import ru.stolexiy.catman.databinding.DialogPurposeAddBinding
import ru.stolexiy.catman.ui.dialog.AbstractBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.adapter.TextWithColorAdapter
import ru.stolexiy.catman.ui.dialog.custom.DatePicker
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import timber.log.Timber

class AddPurposeDialog : AbstractBottomDialogFragment(R.layout.dialog_purpose_add) {

    private lateinit var mAddingPurpose: Purpose

    private val mViewModel: AddPurposeViewModel by viewModels { AddPurposeViewModel.Factory }
    private val mBinding: DialogPurposeAddBinding
        get() = binding!! as DialogPurposeAddBinding

    private val mDeadlineDialog: DatePicker by lazy {
        initChooseDeadlineDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreState()
        mBinding.apply {
            purpose = mAddingPurpose
            lifecycleOwner = this@AddPurposeDialog
            purposeDeadlineLayout.setStartIconOnClickListener { chooseDeadline() }
            purposeDeadline.setOnClickListener { chooseDeadline() }
            purposeCategory.onItemClickListener = onSelectCategory()
            addPurposeButton.setOnClickListener { addPurpose() }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.state.collect { handleState(it) }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState()
    }

    private fun restoreState() {
        mAddingPurpose = findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.get<Purpose>(ADDING_PURPOSE)
            ?: Purpose()
    }

    private fun saveState() {
        findNavController().previousBackStackEntry?.savedStateHandle?.let {
            it[ADDING_PURPOSE] = mAddingPurpose
        }
    }

    private fun handleState(newState: AddPurposeViewModel.State) {
        when (newState) {
            is AddPurposeViewModel.State.Init -> {
                Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
            }
            is AddPurposeViewModel.State.Loaded -> {
                onLoadedNewState(newState)
            }
            is AddPurposeViewModel.State.Error -> {
                Toast.makeText(requireContext(), "Error...", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onLoadedNewState(newState: AddPurposeViewModel.State.Loaded) {
        newState.categories.let { categories ->
            mBinding.purposeCategory.setAdapter(
                TextWithColorAdapter(categories, requireContext()) {
                    TextWithColorAdapter.Item(it.id, it.color, it.name)
                }
            )
            if (categories.isNotEmpty()) {
                val selected = mAddingPurpose.category.get() ?: categories.first()
                val selectedIdx = categories.indexOf(selected).takeIf { it >= 0 } ?: 0
                mBinding.purposeCategory.listSelection = selectedIdx
            }
        }
    }

    private fun addPurpose() {
        if (mAddingPurpose.isValid) {
            mViewModel.addPurpose(mAddingPurpose)
            mAddingPurpose = Purpose()
            saveState()
            dismissNow()
        }
    }
    private fun initChooseDeadlineDialog(): DatePicker {
        val datePicker = DatePicker(
            R.string.deadline,
            selection = mAddingPurpose.deadline.get()?.timeInMillis ?: System.currentTimeMillis(),
            condition = mAddingPurpose.deadline.condition
        )
        datePicker.dialog.addOnPositiveButtonClickListener {
            mAddingPurpose.deadline.set(it.toCalendar())
        }
        return datePicker
    }

    private fun chooseDeadline() {
        if (mDeadlineDialog.dialog.isResumed)
            return
        Timber.d("choose deadline")
        mDeadlineDialog.dialog.show(childFragmentManager, "CHOOSE_DEADLINE")
    }

    private fun onSelectCategory(): AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selection =
                (parent!!.getItemAtPosition(position) as TextWithColorAdapter.Item).toCategory()
            mAddingPurpose.category.set(selection)
        }

    private companion object {
        const val ADDING_PURPOSE = "ADDING_PURPOSE"
    }
}