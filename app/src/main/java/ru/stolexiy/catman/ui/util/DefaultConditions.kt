package ru.stolexiy.catman.ui.util

import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.validation.Conditions

object DefaultConditions {
    fun <T> notNull() = Conditions.NotNull<T>(R.string.required_field_error)
    fun notEmpty() = Conditions.RequiredField<String>(R.string.required_field_error)
    fun dateTimeFromToday() = Conditions.DateTimeRange.fromToday(R.string.deadline_cant_be_past)
    fun dateFromToday() = Conditions.DateRange.fromToday(R.string.deadline_cant_be_past)
    fun positiveInt() =
        Conditions.IntRange(min = 0, errorStringRes = R.string.required_greater_int)


}
