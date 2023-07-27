package ru.stolexiy.catman.ui.util.snackbar

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarManager @Inject constructor() {
    private val snackbars: Queue<Snackbar> = LinkedList()
    private val callback = SnackbarCallback()

    private fun createSnackbar(view: View): Snackbar {
        return Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE).also {
            snackbars.add(it)
            it.addCallback(callback)
        }
    }

    fun addSnackbar(view: View, setUp: Snackbar.() -> Unit) {
        createSnackbar(view).apply {
            setUp()
            if (snackbars.size <= 1)
                show()
        }
    }

    fun replaceOrAddSnackbar(view: View, setUp: Snackbar.() -> Unit) {
        snackbars.peek()?.dismiss()
        val snackbar = createSnackbar(view)
        snackbar.setUp()
        snackbar.show()
    }

    private inner class SnackbarCallback : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onShown(snackbar: Snackbar) {
            snackbar.removeCallback(this)
            snackbars.apply {
                poll()
                peek()?.show()
            }
        }
    }
}
