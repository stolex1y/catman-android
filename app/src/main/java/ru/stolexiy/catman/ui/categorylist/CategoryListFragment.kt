package ru.stolexiy.catman.ui.categorylist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.stolexiy.catman.CatmanApplication
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.FragmentCategoryListBinding
import ru.stolexiy.catman.ui.categorylist.model.CategoryListFragmentState
import ru.stolexiy.catman.ui.categorylist.model.CategoryListItem
import ru.stolexiy.catman.ui.dialog.addPurpose.AddPurposeBottomDialogFragment
import timber.log.Timber

class CategoryListFragment : Fragment() {

    private val viewModel: CategoryListViewModel by viewModels {
        CategoryListViewModel.Factory(
            this,
            Dispatchers.Default,
            (requireActivity().application as CatmanApplication).categoryRepository
        )
    }

    private var _binding: FragmentCategoryListBinding? = null
    private val binding: FragmentCategoryListBinding
        get() = _binding!!

    private val adapter = CategoryListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        savedInstanceState?.apply {
            Timber.d("saved state ${this}")
         }
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = viewModel
            lifecycleOwner = this@CategoryListFragment.viewLifecycleOwner
            categoryList.adapter = adapter
            addPurposeButton.setOnClickListener {
                addPurposeDialog()
            }
            addTaskButton.setOnClickListener {
                val action = CategoryListFragmentDirections.actionCategoryListFragmentToTaskListFragment(1)
                findNavController().navigate(action)
            }
        }
        observeState()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onDataLoaded(data: List<CategoryListItem>) = adapter.submitList(data)

    private fun onLoading() {
        Toast.makeText(requireContext(), "Data is loading...", Toast.LENGTH_LONG).show()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state1.collect {
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

    private fun addPurposeDialog() {
        val dialog = AddPurposeBottomDialogFragment()
        dialog.show(childFragmentManager, "ADD_PURPOSE")
    }
}