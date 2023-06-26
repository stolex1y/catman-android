package ru.stolexiy.catman.ui.dialog.custom

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable.Creator
import androidx.annotation.StringRes
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.validation.Condition
import ru.stolexiy.catman.ui.util.validation.Condition.Companion.isValid
import ru.stolexiy.common.DateUtils.toEpochMillis
import ru.stolexiy.common.DateUtils.toZonedDateTime
import ru.stolexiy.common.DateUtils.todayLastMoment
import java.io.Serializable
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DatePicker(
    @StringRes title: Int,
    selection: Long = System.currentTimeMillis(),
    isValid: Validator
) {

    constructor(
        @StringRes title: Int,
        selection: ZonedDateTime = todayLastMoment(),
        condition: Condition<ZonedDateTime?>
    ) : this(
        title,
        selection.toEpochMillis() + selection.offset.totalSeconds * 1000,
        { condition.isValid(it.toZonedDateTime(ZoneOffset.UTC)) })

    constructor(
        @StringRes title: Int,
        selection: Long = System.currentTimeMillis(),
        min: Long = 0,
        max: Long = Long.MAX_VALUE
    ) : this(title, selection, { it in min..max })

    val dialog: MaterialDatePicker<Long>

    init {
        dialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setSelection(selection)
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(
                    DateValidator(isValid)
                ).build()
            )
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setPositiveButtonText(R.string.choose)
            .setNegativeButtonText(R.string.cancel)
            .build()
    }

    data class DateValidator(
        val validator: Validator
    ) : CalendarConstraints.DateValidator {

        companion object CREATOR : Creator<DateValidator?> {

            private val VALIDATOR = "VALIDATOR"
            override fun createFromParcel(source: Parcel): DateValidator? {
                return source.readBundle(CREATOR::class.java.classLoader)
                    ?.getSerializable(VALIDATOR)?.let {
                        DateValidator(it as Validator)
                    }
            }

            override fun newArray(size: Int): Array<DateValidator?> {
                return arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeBundle(Bundle().apply { putSerializable(VALIDATOR, validator) })
        }

        override fun isValid(date: Long): Boolean {
            return validator.isValid(date)
        }
    }

    fun interface Validator : Serializable {
        fun isValid(date: Long): Boolean
    }

}