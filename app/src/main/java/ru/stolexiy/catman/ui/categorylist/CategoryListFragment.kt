package ru.stolexiy.catman.ui.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.FragmentCategoryListBinding
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.dialog.purpose.add.AddPurposeDialog
import ru.stolexiy.catman.ui.util.binding.BindingDelegate.Companion.bindingDelegate
import ru.stolexiy.catman.ui.util.fragment.repeatOnViewLifecycle
import ru.stolexiy.catman.ui.util.recyclerview.ItemActionListener
import ru.stolexiy.catman.ui.util.snackbar.SnackbarManager
import ru.stolexiy.catman.ui.util.viewmodel.CustomAbstractSavedStateViewModelFactory.Companion.assistedViewModels
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class CategoryListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: CategoryListViewModel.Factory

    @Inject
    lateinit var snackbarManager: SnackbarManager

    private val viewModel: CategoryListViewModel by assistedViewModels({ viewModelFactory })

    private val binding: FragmentCategoryListBinding by bindingDelegate()

    private val listAdapter = CategoryListAdapter()

    private var addPurposeDialog: AddPurposeDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dispatchEvent(CategoryListEvent.Load)
        binding.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            listAdapter.apply {
                longPressDragEnabled = true
                setCategoryActionListener(categoryActionListener())
                setPurposeActionListener(purposeActionListener())
            }
            categoryList.adapter = listAdapter
            addPurposeButton.setOnClickListener {
                showAddPurposeDialog()
            }
        }
        observeData()
        observeState()
    }

    private fun observeState() {
        repeatOnViewLifecycle {
            viewModel.state.collect { state ->
                when (state) {
                    is CategoryListViewModel.State.Error -> onError(state.error)
                    is CategoryListViewModel.State.Init -> {}
                    is CategoryListViewModel.State.Loaded -> {}
                    is CategoryListViewModel.State.Deleting -> showDeletingPurposeSnackbar()
                    is CategoryListViewModel.State.Deleted -> showDeletedPurposeSnackbar()
                    is CategoryListViewModel.State.Added -> showCancelledSnackbar()
                    is CategoryListViewModel.State.Adding -> showCancellingSnackbar()
                    is CategoryListViewModel.State.Canceled -> showCancelledSnackbar()
                }
            }
        }
    }

    private fun observeData() {
        repeatOnViewLifecycle {
            viewModel.data.collectLatest { data ->
                listAdapter.submitList(data.categories.toMutableList())
            }
        }
    }

    private fun onError(@StringRes msg: Int) {
        snackbarManager.addSnackbar(requireView()) {
            setText(msg)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showDeletingPurposeSnackbar() {
        snackbarManager.replaceOrAddSnackbar(requireView()) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(CategoryListEvent.Cancel) }
            setText(R.string.purpose_deleting)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showCancellingSnackbar() {
        snackbarManager.replaceOrAddSnackbar(requireView()) {
            setText(R.string.cancelling)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showDeletedPurposeSnackbar() {
        snackbarManager.replaceOrAddSnackbar(requireView()) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(CategoryListEvent.Add) }
            setText(R.string.purpose_deleted)
            duration = Snackbar.LENGTH_SHORT
        }
    }

    private fun showCancelledSnackbar() {
        snackbarManager.replaceOrAddSnackbar(requireView()) {
            setText(R.string.cancelled)
            duration = Snackbar.LENGTH_SHORT
        }
    }

    private fun showAddPurposeDialog() {
        if (addPurposeDialog == null) {
            addPurposeDialog = AddPurposeDialog(
                onDestroyDialog = { this@CategoryListFragment.addPurposeDialog = null }
            ).apply {
                show(this@CategoryListFragment.childFragmentManager, "ADD_PURPOSE")
            }
        }
    }

    private fun categoryActionListener(): ItemActionListener<CategoryListItem> {
        return object : ItemActionListener<CategoryListItem> {

        }
    }

    private fun purposeActionListener(): ItemActionListener<CategoryListItem> {
        return object : ItemActionListener<CategoryListItem> {
            override fun onSwipeToEnd(item: CategoryListItem) {
                item as CategoryListItem.PurposeItem
                Timber.d("Delete item with name: ${item.name}")
                deletePurpose(item.id)
            }

            override fun onSwipeToStart(item: CategoryListItem) {
                item as CategoryListItem.PurposeItem
                Timber.d("Delete item with name: '${item.name}'")
                deletePurpose(item.id)
            }

            override fun onMoveTo(item: CategoryListItem, to: CategoryListItem) {
                viewModel.dispatchEvent(
                    CategoryListEvent.SwapPriority(item.id, to.id)
                )
                item as CategoryListItem.PurposeItem
                to as CategoryListItem.PurposeItem
                Timber.d("Swap items: '${item.name}' and '${to.name}'")
            }
        }
    }

    private fun deletePurpose(id: Long) {
        viewModel.dispatchEvent(CategoryListEvent.Delete(id))
    }
}
