package ru.stolexiy.catman.ui.dialog.custom

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import ru.stolexiy.catman.R
import ru.stolexiy.common.timer.Time

class TimePicker(
    @StringRes title: Int,
    selection: Time = Time.ZERO,
    onPositiveButtonClickListener: (Time) -> Unit
) {

    val dialog: MaterialTimePicker

    init {
        dialog = MaterialTimePicker.Builder()
            .setTitleText(title)
            .setHour(selection.h)
            .setMinute(selection.minCeil())
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setPositiveButtonText(R.string.choose)
            .setNegativeButtonText(R.string.cancel)
            .build()

        dialog.addOnPositiveButtonClickListener {
            onPositiveButtonClickListener(Time.from(h = dialog.hour, min = dialog.minute))
        }
    }

    fun show(fragmentManager: FragmentManager, tag: String?) {
        if (dialog.isResumed)
            return
        dialog.show(fragmentManager, tag)
    }
}
