package ru.stolexiy.common

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

object Delegates {
    fun <O : Any, P> lateinitObjectProperty(
        objectProvider: () -> O?,
        propertyProvider: KMutableProperty1<O, P>,
        defaultValue: P
    ): ReadWriteProperty<Any, P> {
        return object : ReadWriteProperty<Any, P> {
            override fun getValue(thisRef: Any, property: KProperty<*>): P {
                return objectProvider()?.let { obj ->
                    propertyProvider.get(obj)
                } ?: defaultValue
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: P) {
                objectProvider()?.let { obj ->
                    propertyProvider.set(obj, value)
                }
            }
        }
    }
}
