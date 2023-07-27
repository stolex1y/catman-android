package ru.stolexiy.catman.ui.details.purpose

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.stolexiy.catman.ui.categorylist.CategoryListViewModel
import ru.stolexiy.catman.ui.util.snackbar.SnackbarManager
import javax.inject.Inject

@AndroidEntryPoint
class PurposeDetailsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: CategoryListViewModel.Factory

    @Inject
    lateinit var snackbarManager: SnackbarManager
}