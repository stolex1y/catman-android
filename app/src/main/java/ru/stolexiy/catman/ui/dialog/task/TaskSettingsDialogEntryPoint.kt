package ru.stolexiy.catman.ui.dialog.task

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ru.stolexiy.catman.ui.util.snackbar.SnackbarManager

@EntryPoint
@InstallIn(FragmentComponent::class)
interface TaskSettingsDialogEntryPoint {
    fun taskSettingsViewModelFactory(): TaskSettingsViewModel.Factory
    fun snackbarManager(): SnackbarManager
}
