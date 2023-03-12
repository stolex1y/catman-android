package ru.stolexiy.catman.ui.util.notification

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.Data
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.state.ActionInfo
import timber.log.Timber

fun View.showLiveWorkInfoSnackbar(
    lifecycleOwner: LifecycleOwner,
    appContext: Context,
    viewModelState: StateFlow<ActionInfo>,
    @StringRes runningMsg: Int,
    @StringRes successMsg: Int,
    @StringRes failedMsg: Int = R.string.execution_failed,
    onRevertResult: ((Data) -> Unit)? = null
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            val snackbar =
                Snackbar.make(this@showLiveWorkInfoSnackbar, runningMsg, Snackbar.LENGTH_INDEFINITE)
            viewModelState.collect { state ->
                when (state) {
                    is ActionInfo.Loaded -> {
                        snackbar.setText(successMsg)
                        if (onRevertResult != null && state.workInfo != null) {
                            snackbar.setAction(R.string.cancel) { _ ->
                                Timber.d("result cancel")
                                onRevertResult(state.workInfo.outputData)
                            }
                        }
                        snackbar.duration = Snackbar.LENGTH_LONG
                        snackbar.show()
                    }
                    is ActionInfo.Error -> {
                        snackbar.setText(failedMsg)
                        snackbar.duration = Snackbar.LENGTH_SHORT
                        snackbar.show()
                    }
                    is ActionInfo.Loading -> {
                        snackbar.setText(runningMsg)
                        snackbar.duration = Snackbar.LENGTH_INDEFINITE
                        snackbar.setAction(R.string.cancel) {  _ ->
                            Timber.d("running cancel")
                            WorkManager.getInstance(appContext).cancelWorkById(workInfo.id)
                        }
                        snackbar.show()
                    }
                    else -> {}
                }
                if (workInfo.state.isFinished)
                    cancel()
            }
        }
    }
}