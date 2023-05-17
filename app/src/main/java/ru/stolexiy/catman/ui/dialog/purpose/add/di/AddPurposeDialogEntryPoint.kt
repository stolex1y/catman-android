package ru.stolexiy.catman.ui.dialog.purpose.add.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ru.stolexiy.catman.ui.dialog.purpose.add.AddPurposeViewModel

@EntryPoint
@InstallIn(FragmentComponent::class)
interface AddPurposeDialogEntryPoint {
    fun assistedViewModelFactory(): AddPurposeViewModel.Factory
}