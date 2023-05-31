package ru.stolexiy.catman.ui.dialog.category.add

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.DialogCategoryAddBinding
import ru.stolexiy.catman.ui.dialog.AbstractBottomDialogFragment
import ru.stolexiy.catman.ui.dialog.category.add.di.AddCategoryDialogEntryPoint
import ru.stolexiy.catman.ui.dialog.category.model.Category
import ru.stolexiy.catman.ui.dialog.category.model.Color
import ru.stolexiy.catman.ui.dialog.category.model.ColorWithNameAdapter
import ru.stolexiy.catman.ui.util.binding.BindingDelegate.Companion.bindingDelegate
import ru.stolexiy.catman.ui.util.di.entryPointAccessors
import ru.stolexiy.catman.ui.util.fragment.repeatOnParentViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.repeatOnViewLifecycle
import ru.stolexiy.catman.ui.util.fragment.requireParentView
import ru.stolexiy.catman.ui.util.viewmodel.CustomAbstractSavedStateViewModelFactory.Companion.assistedViewModels

class AddCategoryDialog(
    onDestroyDialog: () -> Unit = {}
) : AbstractBottomDialogFragment(R.layout.dialog_category_add, onDestroyDialog) {

    private lateinit var addingCategory: Category

    private val entryPointProvider: AddCategoryDialogEntryPoint by entryPointAccessors()

    private val viewModel: AddCategoryViewModel by assistedViewModels({
        entryPointProvider.addCategoryViewModelFactory()
    })

    private val binding: DialogCategoryAddBinding by bindingDelegate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dispatchEvent(AddCategoryEvent.Load)
        restoreState()
        binding.apply {
            category = addingCategory
            lifecycleOwner = viewLifecycleOwner
            categoryColor.onItemClickListener = onColorClickListener()
            addCategoryButton.setOnClickListener { addCategory() }
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
        addingCategory = findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.get<Category>(ADDING_CATEGORY)
            ?: Category()
    }

    private fun saveState() {
        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            it[ADDING_CATEGORY] = addingCategory
        }
    }

    private fun clearState() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.remove<Category>(ADDING_CATEGORY)
    }

    private fun observeStateWithParentView() {
        val parentView = requireParentView()
        repeatOnParentViewLifecycle {
            viewModel.state.collect { newState ->
                when (newState) {
                    AddCategoryViewModel.State.Adding -> showAddingCategorySnackbar(parentView)

                    AddCategoryViewModel.State.Added -> showAddedCategorySnackbar(parentView)

                    AddCategoryViewModel.State.Deleting -> showCancellingSnackbar(parentView)

                    AddCategoryViewModel.State.Deleted -> showCancelledSnackbar(parentView)

                    AddCategoryViewModel.State.Canceled -> showCancelledSnackbar(parentView)

                    else -> {}
                }
            }
        }
    }

    private fun showAddingCategorySnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(AddCategoryEvent.Cancel) }
            setText(R.string.category_adding)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showAddedCategorySnackbar(parentView: View) {
        entryPointProvider.snackbarManager().replaceOrAddSnackbar(parentView) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(AddCategoryEvent.DeleteAdded) }
            setText(R.string.category_added)
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
                val colors = data.colors
                binding.categoryColor.setAdapter(
                    ColorWithNameAdapter(colors, requireContext())
                )
                if (colors.isNotEmpty()) {
                    val selected = addingCategory.color.get() ?: colors.first()
                    val selectedIdx = colors.indexOf(selected).takeIf { it >= 0 } ?: 0
                    binding.categoryColor.listSelection = selectedIdx
                }
            }
        }
    }

    private fun addCategory() {
        viewModel.dispatchEvent(AddCategoryEvent.Add(addingCategory))
        clearState()
        dismissNow()
    }

    private fun onColorClickListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selection = parent!!.getItemAtPosition(position) as Color
            addingCategory.color.set(selection)
            binding.categoryColor.setText(selection.name)
        }

    private companion object {
        const val ADDING_CATEGORY = "ADDING_CATEGORY"
    }
}
