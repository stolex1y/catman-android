package ru.stolexiy.catman.ui.dialog.purpose.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.core.text.toSpanned
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.R
import ru.stolexiy.catman.core.ViewModelFactory
import ru.stolexiy.catman.databinding.DialogAddPurposeBinding
import ru.stolexiy.catman.ui.dialog.AddEntityBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.adapter.TextWithColorAdapter
import ru.stolexiy.catman.ui.dialog.purpose.PurposeSettingsViewModel
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.catman.ui.dialog.valiador.RequiredTextValidator
import ru.stolexiy.catman.ui.mapper.calendarFromMillis
import ru.stolexiy.catman.ui.mapper.toDmyString

class AddPurposeBottomDialogFragment(
    onDismiss: () -> Unit = {},
    onCancel: () -> Unit = {}
) : AddEntityBottomDialogFragment(R.layout.dialog_add_purpose, onDismiss, onCancel) {

    companion object {
        val ADDING_PURPOSE: String = "ADDING_PURPOSE"
    }

    private lateinit var savedStateHandle: SavedStateHandle
    private val viewModel: PurposeSettingsViewModel by viewModels {
        ViewModelFactory(this, requireActivity().application as CatmanApplication)
    }

    private val binding: DialogAddPurposeBinding
        get() = _binding!! as DialogAddPurposeBinding

    private lateinit var validators: List<RequiredTextValidator<EditText>>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedStateHandle = findNavController().currentBackStackEntry!!.savedStateHandle
        restoreInputState()?.let { viewModel.purpose = it }
        binding.purpose = viewModel.purpose
        initRequiredFieldHint()
        initValidators()
        binding.apply {
            purposeDeadlineLayout.setStartIconOnClickListener {
                chooseDeadline()
            }
            purposeDeadline.setOnClickListener { chooseDeadline() }
//            purposeCategory.setOnClickListener { purposeCategory.showDropDown() }
            purposeCategory.onItemClickListener = onSelectCategory()
            addPurposeButton.setOnClickListener { addPurpose() }
            legendText.text = styleAsterisk(legendText.text.toString())
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            binding.purposeCategory.setAdapter (
                TextWithColorAdapter(viewModel.getCategories(), requireContext()) {
                    TextWithColorAdapter.Item(it.id, it.color, it.name)
                }
            )
            binding.purposeCategory.listSelection = viewModel.getCategories().indexOfFirst { it.id == viewModel.purpose.categoryId }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveInputState(viewModel.purpose)
    }

    private fun addPurpose() {
        validators.forEach { it.valueUpdated() }
        val allValid = validators.map { it.isValid }.all { it }
        if (allValid) {
            viewModel.addPurpose()
            dismissNow()
        }
    }

    private fun setChosenCategory(selection: Category) {
        binding.purposeCategory.setText(selection.name)
        binding.purposeCategoryLayout.startIconDrawable?.setTint(selection.color)
        viewModel.purpose.categoryId = selection.id
    }

    private fun saveInputState(purpose: Purpose?) {
        savedStateHandle[ADDING_PURPOSE] = purpose
    }

    private fun restoreInputState(): Purpose? {
        val purpose = savedStateHandle.get<Purpose>(ADDING_PURPOSE)
        purpose?.let { purpose ->
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                setChosenCategory(viewModel.getCategories().first { it.id == purpose.categoryId })
            }
        }
        return purpose
    }

    private fun chooseDeadline() {
        binding.purposeDeadline.isEnabled = false
        val dialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.deadline)
            .setSelection(viewModel.purpose.deadline?.timeInMillis ?: System.currentTimeMillis())
            .setCalendarConstraints(CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build())
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setPositiveButtonText(R.string.choose)
            .setNegativeButtonText(R.string.cancel)
            .build()

        dialog.addOnPositiveButtonClickListener {
            viewModel.purpose.deadline = calendarFromMillis(it)
            binding.purposeDeadlineLayout.error = ""
            binding.purposeDeadline.setText(viewModel.purpose.deadline?.toDmyString() ?: "")
        }
        dialog.addOnDismissListener {
            if (viewModel.purpose.deadline == null)
                binding.purposeDeadlineLayout.error = getString(R.string.required_field_error)
        }

        dialog.show(childFragmentManager, "CHOOSE_DEADLINE")
        binding.purposeDeadline.isEnabled = false
    }

    private fun initValidators() {
        val nameValidator = RequiredTextValidator(binding.purposeNameLayout, binding.purposeName)
        val categoryValidator = RequiredTextValidator(binding.purposeCategoryLayout, binding.purposeCategory)
        val deadlineValidator = RequiredTextValidator(binding.purposeDeadlineLayout, binding.purposeDeadline)
        validators = listOf(nameValidator, categoryValidator, deadlineValidator)
    }

    private fun onSelectCategory(): AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selection = (parent!!.getItemAtPosition(position) as TextWithColorAdapter.Item).toCategory()
            setChosenCategory(selection)
        }


    private fun initRequiredFieldHint() {
        binding.purposeNameLayout.apply {
            hint = addAsteriskToEnd(this)
        }
        binding.purposeCategoryLayout.apply {
            hint = addAsteriskToEnd(this)
        }
        binding.purposeDeadlineLayout.apply {
            hint = addAsteriskToEnd(this)
        }
    }

    private fun addAsteriskToEnd(textInput: TextInputLayout): Spanned = styleAsterisk("${textInput.hint.toString()} *")

    @SuppressLint("ResourceType")
    private fun styleAsterisk(text: String): Spanned {
        val spannable = SpannableString(text)
        val start = text.indexOf("*")
        val color: Int = resources.getColor(R.color.pink)
        spannable.setSpan(
            ForegroundColorSpan(color), start, start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable.toSpanned()
    }
}