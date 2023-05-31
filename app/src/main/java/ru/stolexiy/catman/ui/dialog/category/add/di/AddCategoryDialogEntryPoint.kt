package ru.stolexiy.catman.ui.dialog.category.add.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ru.stolexiy.catman.ui.dialog.category.add.AddCategoryViewModel
import ru.stolexiy.catman.ui.util.snackbar.SnackbarManager

@EntryPoint
@InstallIn(FragmentComponent::class)
interface AddCategoryDialogEntryPoint {
    fun addCategoryViewModelFactory(): AddCategoryViewModel.Factory
    fun snackbarManager(): SnackbarManager
}
