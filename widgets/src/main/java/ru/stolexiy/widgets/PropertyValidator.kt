package ru.stolexiy.widgets

fun interface PropertyValidator<T> {
    fun isValid(value: T): Boolean
}