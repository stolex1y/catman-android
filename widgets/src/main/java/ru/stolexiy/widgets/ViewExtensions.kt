package ru.stolexiy.widgets

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ViewExtensions {
    fun View.repeatOnLifecycle(
        state: Lifecycle.State,
        block: suspend CoroutineScope.() -> Unit
    ) {
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            findViewTreeLifecycleOwner()?.repeatOnLifecycle(state) {
                this.run { block() }
            }
        }
    }
}
