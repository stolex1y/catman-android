package ru.stolexiy.catman

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CategoryListFragment : Fragment() {

    companion object {
        fun newInstance() = ru.stolexiy.catman.CategoryListFragment()
    }

    private lateinit var viewModel: ru.stolexiy.catman.CategoryListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ru.stolexiy.catman.CategoryListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}