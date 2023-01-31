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
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.dialog.purpose.add.AddPurposeDialog

class CategoryListFragment : Fragment() {

    private val mViewModel: CategoryListViewModel by viewModels { CategoryListViewModel.Factory }

    private var binding: FragmentCategoryListBinding? = null
    private val mBinding: FragmentCategoryListBinding
        get() = binding!!

    private val mAdapter = CategoryListAdapter()

    private var mAddPurposeDialog: AddPurposeDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_list, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.viewModel = mViewModel
        mBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            categoryList.adapter = mAdapter
            addPurposeButton.setOnClickListener {
                showAddPurposeDialog()
            }
        }
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mViewModel.state.collect {
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

    private fun onDataLoaded(data: List<CategoryListItem>) = mAdapter.submitList(data as MutableList<CategoryListItem>)

    private fun onLoading() {
//        Toast.makeText(requireContext(), "Data is loading...", Toast.LENGTH_LONG).show()
    }

    private fun onError() {
        Toast.makeText(requireContext(), "Error...", Toast.LENGTH_LONG).show()
    }

    private fun showAddPurposeDialog() {
        if (mAddPurposeDialog == null) {
            mAddPurposeDialog = AddPurposeDialog(
                onDestroyDialog = { this@CategoryListFragment.mAddPurposeDialog = null }
            ).apply {
                show(this@CategoryListFragment.childFragmentManager, "ADD_PURPOSE")
            }
        }
    }
}