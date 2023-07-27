package ru.stolexiy.catman.ui.dialog.custom

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable.Creator
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.dialog.custom.DatePicker.Validator
import ru.stolexiy.catman.ui.util.validation.Condition
import ru.stolexiy.catman.ui.util.validation.Condition.Companion.isValid
import ru.stolexiy.catman.ui.util.validation.ValidationResult
import ru.stolexiy.common.DateUtils.toEpochMillis
import ru.stolexiy.common.DateUtils.toLocalDate
import java.io.Serializable
import java.time.LocalDate
import java.time.ZoneOffset

class DatePicker(
    @StringRes title: Int,
    selection: LocalDate = LocalDate.now(),
    validator: Validator = Validator { true },
    onPositiveButtonClickListener: (LocalDate) -> Unit
) {

    constructor(
        @StringRes title: Int,
        selection: LocalDate = LocalDate.now(),
        condition: Condition<LocalDate?> = Condition { ValidationResult.valid() },
        onPositiveButtonClickListener: (LocalDate) -> Unit
    ) : this(
        title,
        selection,
        Validator { condition.isValid(it.toLocalDate(ZoneOffset.UTC)) },
        onPositiveButtonClickListener
    )

    val dialog: MaterialDatePicker<Long>

    init {
        dialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setSelection(selection.toEpochMillis())
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(
                    DateValidator(validator)
                ).build()
            )
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setPositiveButtonText(R.string.choose)
            .setNegativeButtonText(R.string.cancel)
            .build()
        dialog.addOnPositiveButtonClickListener {
            onPositiveButtonClickListener(it.toLocalDate(ZoneOffset.UTC))
        }
    }

    fun show(fragmentManager: FragmentManager, tag: String?) {
        if (dialog.isResumed)
            return
        dialog.show(fragmentManager, tag)
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
