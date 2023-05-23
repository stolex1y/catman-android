package ru.stolexiy.catman.ui.dialog.purpose.edit

import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.dialog.AbstractBottomDialogFragment

class EditPurposeDialog(purposeId: Long) :
    AbstractBottomDialogFragment(R.layout.dialog_purpose_edit) {

    /*private val mViewModel: EditPurposeViewModel by viewModels { EditPurposeViewModel.createFactory(purposeId) }
    private var mUpdatingPurpose: Purpose? = null

    private val mBinding: DialogPurposeEditBinding
        get() = binding!! as DialogPurposeEditBinding

    private val mChooseDeadlineDialog: DatePicker by lazy {
        initChooseDeadlineDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreState()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mViewModel.state.collectLatest { handleState(it) }
                }
                launch {
                    mViewModel.categories.collectLatest { onUpdateCategories(it) }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState()
    }

    private fun restoreState() {
        findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.get<Purpose>(UPDATING_PURPOSE)
            ?.let { mUpdatingPurpose = it }
    }

    private fun saveState() {
        findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.let { it[UPDATING_PURPOSE] = mUpdatingPurpose }
    }

    private fun handleState(newState: EditPurposeViewModel.State) {
        when (newState) {
            is EditPurposeViewModel.State.Init -> {
                disableForm()
                Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
            }

            is EditPurposeViewModel.State.Loading -> {
                disableForm()
                Toast.makeText(requireContext(), "Updating...", Toast.LENGTH_SHORT).show()
            }

            is EditPurposeViewModel.State.Loaded -> {
                if (mUpdatingPurpose == null)
                    mUpdatingPurpose = newState.updatingPurpose
                mBinding.apply {
                    purpose = mUpdatingPurpose
                    lifecycleOwner = viewLifecycleOwner
                    purposeDeadlineLayout.setStartIconOnClickListener { chooseDeadline() }
                    purposeDeadline.setOnClickListener { chooseDeadline() }
                    purposeCategory.onItemClickListener = onSelectCategory()
                    savePurposeButton.setOnClickListener { updatePurpose() }
                    deletePurposeButton.setOnClickListener { deletePurpose() }
                }
                enableForm()
            }

            is EditPurposeViewModel.State.Deleted -> {
                dismiss()
            }

            is EditPurposeViewModel.State.Error -> {
                disableForm()
                Toast.makeText(requireContext(), "Error...", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun disableForm() {
        mBinding.editPurposeForm.isClickable = false
    }

    private fun enableForm() {
        mBinding.editPurposeForm.isClickable = true
    }

    private fun onUpdateCategories(categories: List<Category>) {
        mBinding.purposeCategory.setAdapter(
            TextWithColorAdapter(categories, requireContext()) {
                TextWithColorAdapter.Item(it.id, it.color, it.name)
            }
        )
        if (categories.isNotEmpty()) {
            val selected = mUpdatingPurpose?.category?.get() ?: categories.first()
            val selectedIdx = categories.indexOf(selected).takeIf { it >= 0 } ?: 0
            mBinding.purposeCategory.listSelection = selectedIdx
        }
    }

    private fun updatePurpose() {
        mUpdatingPurpose?.let {
            if (it.isValid)
                mViewModel.updatePurpose(it)
        }
    }

    private fun deletePurpose() {
        mViewModel.deletePurpose()
        dismiss()
    }

    private fun initChooseDeadlineDialog(): DatePicker {
        val datePicker = DatePicker(
            R.string.deadline,
            selection = mUpdatingPurpose?.deadline?.get()?.timeInMillis ?: System.currentTimeMillis(),
            condition = mUpdatingPurpose?.deadline?.condition ?: DefaultConditions.fromToday()
        )
        datePicker.dialog.addOnPositiveButtonClickListener {
            mUpdatingPurpose?.deadline?.set(it.toCalendar())
        }
        return datePicker
    }

    private fun chooseDeadline() {
        if (mChooseDeadlineDialog.dialog.isResumed)
            return
        Timber.d("choose deadline")
        mChooseDeadlineDialog.dialog.show(childFragmentManager, "CHOOSE_DEADLINE")
    }

    private fun onSelectCategory(): AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selection =
                (parent!!.getItemAtPosition(position) as TextWithColorAdapter.Item).toCategory()
            mUpdatingPurpose?.category?.set(selection)
        }

    private companion object {
        const val UPDATING_PURPOSE = "UPDATING_PURPOSE"
    }*/


}
