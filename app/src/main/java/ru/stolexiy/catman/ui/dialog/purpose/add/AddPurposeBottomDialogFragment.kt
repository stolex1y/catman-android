package ru.stolexiy.catman.ui.dialog.purpose.add

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.DialogAddPurposeBinding
import ru.stolexiy.catman.ui.dialog.AddEntityBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.adapter.TextWithColorAdapter
import ru.stolexiy.catman.ui.dialog.custom.DatePicker
import ru.stolexiy.catman.ui.dialog.purpose.PurposeSettingsViewModel
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.mapper.calendarFromMillis
import ru.stolexiy.catman.ui.mapper.toDmyString
import ru.stolexiy.catman.ui.util.validators.ValidatedForm
import ru.stolexiy.catman.ui.util.validators.viewbinders.validateBy
import timber.log.Timber

class AddPurposeBottomDialogFragment(
    onDismiss: () -> Unit = {},
    onCancel: () -> Unit = {}
) : AddEntityBottomDialogFragment(R.layout.dialog_add_purpose, onDismiss, onCancel) {

    private val viewModel: PurposeSettingsViewModel by viewModels { PurposeSettingsViewModel.Factory }
    private lateinit var validatedForm: ValidatedForm

    private val binding: DialogAddPurposeBinding
        get() = _binding!! as DialogAddPurposeBinding

    private val chooseDeadlineDialog: DatePicker by lazy {
        initChooseDeadlineDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.purpose = viewModel.purpose
        initValidators()
        binding.apply {
            purposeDeadlineLayout.setStartIconOnClickListener { chooseDeadline() }
            purposeDeadline.setOnClickListener { chooseDeadline() }
            purposeCategory.onItemClickListener = onSelectCategory()
            addPurposeButton.setOnClickListener { addPurpose() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { categories ->
                    binding.purposeCategory.setAdapter(
                        TextWithColorAdapter(categories, requireContext()) {
                            TextWithColorAdapter.Item(it.id, it.color, it.name)
                        }
                    )
                    if (categories.isNotEmpty()) {
                        binding.purposeCategory.listSelection =
                            categories.indexOfFirst {
                                it.id == (viewModel.purpose.categoryId ?: it.id)
                            }
//                        setChosenCategory(categories[binding.purposeCategory.getSelection])
                    }
                }
            }
        }
    }

    private fun addPurpose() {
        if (validatedForm.validate().isValid) {
            viewModel.addPurpose()
            dismissNow()
        }
    }

    private fun setChosenCategory(selection: Category) {
        binding.purposeCategory.setText(selection.name, false)
        binding.purposeCategoryLayout.isStartIconVisible = true
        binding.purposeCategoryLayout.startIconDrawable?.setTint(selection.color)
        viewModel.purpose.categoryId = selection.id
    }

    private fun initChooseDeadlineDialog(): DatePicker {
        val startDate = viewModel.purpose.deadline?.timeInMillis ?: System.currentTimeMillis()
        val datePicker = DatePicker(
            R.string.deadline,
            selection = startDate,
            start = startDate
        )
        datePicker.dialog.addOnPositiveButtonClickListener {
            viewModel.purpose.deadline = calendarFromMillis(it)
            binding.purposeDeadline.setText(viewModel.purpose.deadline?.toDmyString() ?: "")
        }
        datePicker.dialog.addOnDismissListener {
            if (viewModel.purpose.deadline == null)
                binding.purposeDeadline.setText("")
        }
        return datePicker
    }

    private fun chooseDeadline() {
        if (chooseDeadlineDialog.dialog.isResumed)
            return
        Timber.d("choose deadline")
        chooseDeadlineDialog.dialog.show(childFragmentManager, "CHOOSE_DEADLINE")
    }

    private fun initValidators() {
        validatedForm = ValidatedForm(
            listOf(
                (binding.purposeName to binding.purposeNameLayout).validateBy(viewModel.requiredFieldCondition),
                (binding.purposeDeadline to binding.purposeDeadlineLayout).validateBy(viewModel.requiredFieldCondition),
                (binding.purposeCategory to binding.purposeCategoryLayout).validateBy(viewModel.requiredFieldCondition),
            )
        )
    }

    private fun onSelectCategory(): AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selection =
                (parent!!.getItemAtPosition(position) as TextWithColorAdapter.Item).toCategory()
            setChosenCategory(selection)
        }

}