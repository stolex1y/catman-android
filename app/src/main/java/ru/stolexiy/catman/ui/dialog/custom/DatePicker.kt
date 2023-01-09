package ru.stolexiy.catman.ui.dialog.custom

import android.os.Parcel
import android.os.Parcelable.Creator
import androidx.annotation.StringRes
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.MaterialDatePicker
import ru.stolexiy.catman.R

class DatePicker(
    @StringRes title: Int,
    selection: Long = System.currentTimeMillis(),
    start: Long = 0,
    end: Long = Long.MAX_VALUE
) {
    val dialog: MaterialDatePicker<Long>

    init {
        dialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setSelection(selection)
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(DateRangeValidator(start, end)).build()
            )
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setPositiveButtonText(R.string.choose)
            .setNegativeButtonText(R.string.cancel)
            .build()
    }

    data class DateRangeValidator(
        val start: Long = 0,
        val end: Long = Long.MAX_VALUE
    ) : DateValidator {

        companion object CREATOR : Creator<DateRangeValidator?> {
            override fun createFromParcel(source: Parcel): DateRangeValidator? {
                return DateRangeValidator(source.readLong(), source.readLong())
            }

            override fun newArray(size: Int): Array<DateRangeValidator?> {
                return arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeLong(start)
            dest.writeLong(end)
        }

        override fun isValid(date: Long): Boolean {
            return date in start..end
        }
    }
}