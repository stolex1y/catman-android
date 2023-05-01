package ru.stolexiy.widgets

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class InvalidatingLayoutProperty<T>(
    initialValue: T,
    private val propertyValidator: PropertyValidator<T>? = null
) : ReadWriteProperty<InvalidatingLayoutProperty.Listener, T> {

    @Volatile
    private var value: T = initialValue

    @AnyThread
    override fun getValue(thisRef: Listener, property: KProperty<*>): T {
        return value
    }

    @MainThread
    override fun setValue(thisRef: Listener, property: KProperty<*>, value: T) {
        if (this.value != value) {
            require(propertyValidator?.isValid(value) ?: true)
            this.value = value
            thisRef.onLayoutInvalidation()
        }
    }

    fun interface Listener {
        fun onLayoutInvalidation()
    }
}