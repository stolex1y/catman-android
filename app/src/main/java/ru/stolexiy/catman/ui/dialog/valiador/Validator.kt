package ru.stolexiy.catman.ui.dialog.valiador

import android.view.View

abstract class Validator<T>(
    val view: View,
) {
    var isValid = false
        private set

    abstract fun isValid(value: T?): Boolean
    protected open fun setListener() {
        view.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                valueUpdated()
        }
    }
    protected abstract fun onError()
    protected abstract fun onSuccess()
    protected abstract fun getValue(): T?
    fun valueUpdated() {
        val value = getValue()
        if (!isValid(value)) {
            isValid = false
            onError()
        } else {
            onSuccess()
            isValid = true
        }
    }
}