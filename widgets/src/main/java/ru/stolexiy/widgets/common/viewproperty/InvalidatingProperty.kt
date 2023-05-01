package ru.stolexiy.widgets.common.viewproperty

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class InvalidatingProperty<T>(
    initialValue: T,
    private val propertyValidator: PropertyValidator<T>? = null
) : ReadWriteProperty<InvalidatingProperty.Listener, T> {

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
            thisRef.onPropertyInvalidation()
        }
    }

    fun interface Listener {
        fun onPropertyInvalidation()
    }
}