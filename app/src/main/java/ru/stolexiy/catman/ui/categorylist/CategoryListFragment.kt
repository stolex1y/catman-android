package ru.stolexiy.catman.ui.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                setOnCategoryClickListener {
                    Timber.d("clicked on category ${(it as CategoryListItem.CategoryItem).name}")
                }
                setOnPurposeClickListener {
                    Timber.d("clicked on purpose ${(it as CategoryListItem.PurposeItem).name}")
                }
                onItemSwipedToEndListener = {
                    Timber.d("delete item with name ${(it as CategoryListItem.PurposeItem).name}")
                    viewModel.dispatchEvent(CategoryListEvent.Delete(it.id))
                }
                onItemMovedListener = { source, target ->
                    viewModel.dispatchEvent(
                        CategoryListEvent.SwapPriority(
                            source.id,
                            target.id
                        )
                    )
                    Timber.d("moved ${source.id} to ${target.id}")
                }
            }
            categoryList.adapter = listAdapter
            addPurposeButton.setOnClickListener {
                showAddPurposeDialog()
            }
        }
        observeData()
        observeState()
    }

    private fun observeData() {
        repeatOnViewLifecycle {
            viewModel.data.collectLatest {
                handleNewData(it)
            }
        }
    }

    private fun observeState() {
        repeatOnViewLifecycle {
            viewModel.state.collect {
                handleState(it)
            }
        }
    }

    private fun handleState(state: CategoryListViewModel.State) {
        when (state) {
            is CategoryListViewModel.State.Error -> onError()
            is CategoryListViewModel.State.Init -> onLoading()
            is CategoryListViewModel.State.Loaded -> {}
            is CategoryListViewModel.State.Deleting -> showDeletingSnackbar()
            is CategoryListViewModel.State.Deleted -> showDeletedSnackbar()
            is CategoryListViewModel.State.Added -> showAddedSnackbar()
            is CategoryListViewModel.State.Adding -> showAddingSnackbar()
        }
    }

    private fun handleNewData(data: CategoryListViewModel.Data) =
        listAdapter.submitList(data.categories.toMutableList())

    private fun onLoading() {
//        Toast.makeText(requireContext(), "Data is loading...", Toast.LENGTH_LONG).show()
    }

    private fun onError() {
        Toast.makeText(requireContext(), "Error...", Toast.LENGTH_LONG).show()
    }

    private fun showDeletingSnackbar() {
        snackbarManager.replaceOrAddSnackbar(requireView()) {
            setText(R.string.purpose_deleting)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showAddingSnackbar() {
        snackbarManager.replaceOrAddSnackbar(requireView()) {
            setText(R.string.cancelling)
            duration = Snackbar.LENGTH_INDEFINITE
        }
    }

    private fun showDeletedSnackbar() {
        snackbarManager.replaceOrAddSnackbar(requireView()) {
            setAction(R.string.cancel) { viewModel.dispatchEvent(CategoryListEvent.Add) }
            setText(R.string.purpose_deleted)
            duration = Snackbar.LENGTH_SHORT
        }
    }

    private fun showAddedSnackbar() {
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
}
