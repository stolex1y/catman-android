package ru.stolexiy.widgets.common.viewproperty

fun interface PropertyValidator<T> {
    fun isValid(value: T): Boolean
}