package ru.stolexiy.catman.ui.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.FragmentCategoryListBinding
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.dialog.purpose.add.AddPurposeDialog
import ru.stolexiy.catman.ui.util.binding.BindingDelegate
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CategoryListFragment : Fragment() {

    private val viewModel: CategoryListViewModel by viewModels()
    private val binding: FragmentCategoryListBinding by BindingDelegate(
        view,
        viewLifecycleOwner.lifecycle
    )

    @Inject
    lateinit var listAdapter: CategoryListAdapter

    private var addPurposeDialog: AddPurposeDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            listAdapter.apply {
                longPressDragEnabled = true
                setOnCategoryClickListener { Timber.d("clicked on category ${(it as CategoryListItem.CategoryItem).name}") }
                setOnPurposeClickListener { Timber.d("clicked on purpose ${(it as CategoryListItem.PurposeItem).name}") }
                onItemSwipedToEndListener = {
                    Timber.d("${it.javaClass.simpleName} swiped")
                }
                onItemMovedListener = { source, target ->
                    Timber.d("moved ${source.id} to ${target.id}")
                }
            }
            categoryList.adapter = listAdapter
            addPurposeButton.setOnClickListener {
                showAddPurposeDialog()
            }
        }
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect {
                    handleState(it)
                }
            }
        }
    }

    private fun handleState(state: CategoryListViewModel.State) {
        when (state) {
            is CategoryListViewModel.State.Error -> onError()
            is CategoryListViewModel.State.Init -> onLoading()
            is CategoryListViewModel.State.Loaded -> onDataLoaded(state.data)
        }
    }

    private fun onDataLoaded(data: List<CategoryListItem>) =
        listAdapter.submitList(data as MutableList<CategoryListItem>)

    private fun onLoading() {
//        Toast.makeText(requireContext(), "Data is loading...", Toast.LENGTH_LONG).show()
    }

    private fun onError() {
        Toast.makeText(requireContext(), "Error...", Toast.LENGTH_LONG).show()
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
