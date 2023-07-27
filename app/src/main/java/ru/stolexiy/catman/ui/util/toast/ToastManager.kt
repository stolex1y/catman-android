/*
package ru.stolexiy.catman.ui.util.toast

import android.content.Context
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToastManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val toasts: Queue<Toast> = LinkedList()
    private val callback = SnackbarCallback()

    private fun createToast(): Toast {
        Toast.makeText(context, "", Toast.LENGTH_LONG).apply {
            addCallback()
            Looper.getMainLooper().
        }
    }

    private inner class ToastCallback {

    }
}*/
