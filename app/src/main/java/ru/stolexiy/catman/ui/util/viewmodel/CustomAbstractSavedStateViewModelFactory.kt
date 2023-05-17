package ru.stolexiy.catman.ui.util.viewmodel

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

@Suppress("UNCHECKED_CAST")
class CustomAbstractSavedStateViewModelFactory<out T : ViewModel>(
    owner: SavedStateRegistryOwner,
    private val viewModelProducer: (SavedStateHandle) -> T,
    defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = viewModelProducer(handle) as T

    companion object {
        inline fun <reified T : ViewModel> Fragment.assistedViewModels(
            noinline viewModelProducer: (SavedStateHandle) -> T
        ) = viewModels<T> {
            CustomAbstractSavedStateViewModelFactory(this, viewModelProducer, this.arguments)
        }
    }
}