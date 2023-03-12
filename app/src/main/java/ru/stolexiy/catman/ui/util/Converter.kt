package ru.stolexiy.catman.ui.util

fun interface Converter<S, T> {
    fun convert(source: S): T
}