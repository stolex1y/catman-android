package ru.stolexiy.widgets.common.viewproperty

internal fun interface PropertyValidator<T> {
    fun isValid(value: T): Boolean
}