package ru.stolexiy.catman.ui.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.FragmentCategoryListBinding
import ru.stolexiy.catman.ui.categorylist.model.CategoryListFragmentState
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.dialog.purpose.add.AddPurposeBottomDialogFragment
import timber.log.Timber

class CategoryListFragment : Fragment() {

    private var dialogIsShowing: Boolean = false

    private val viewModel: CategoryListViewModel by viewModels { CategoryListViewModel.Factory }

    private var _binding: FragmentCategoryListBinding? = null
    private val binding: FragmentCategoryListBinding
        get() = _binding!!

    private val adapter = CategoryListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        savedInstanceState?.apply {
            Timber.d("saved state $this")
         }
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.apply {
            lifecycleOwner = this@CategoryListFragment.viewLifecycleOwner
            categoryList.adapter = adapter
            addPurposeButton.setOnClickListener {
                if (!dialogIsShowing)
                    addPurposeDialog()
            }
        }
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    handleState(it)
                }
            }
        }
    }

    private fun handleState(state: CategoryListFragmentState) {
        when (state) {
            is CategoryListFragmentState.Init -> Unit
            is CategoryListFragmentState.IsLoading -> onLoading()
            is CategoryListFragmentState.LoadedData -> onDataLoaded(state.data)
        }
    }

    private fun onDataLoaded(data: List<CategoryListItem>) = adapter.submitList(data as MutableList<CategoryListItem>)

    private fun onLoading() {
        Toast.makeText(requireContext(), "Data is loading...", Toast.LENGTH_LONG).show()
    }

    private fun addPurposeDialog() {
        val dialog = AddPurposeBottomDialogFragment(onDismiss = { dialogIsShowing = false })
        dialogIsShowing = true
        dialog.show(parentFragmentManager, "ADD_PURPOSE")
    }
}