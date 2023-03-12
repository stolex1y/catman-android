package ru.stolexiy.catman.ui.util

import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.validation.Conditions

object DefaultConditions {
    fun <T> notNull() = Conditions.NotNull<T>(R.string.required_field_error)
    fun notEmpty() = Conditions.RequiredField<String>(R.string.required_field_error)
    fun fromToday() = Conditions.DateRange.fromToday(R.string.deadline_cant_be_past)
}