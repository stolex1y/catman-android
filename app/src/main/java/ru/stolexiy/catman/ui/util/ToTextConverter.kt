package ru.stolexiy.catman.ui.util

fun interface ToTextConverter<S> : Converter<S, String> {
    override fun convert(source: S): String
}
