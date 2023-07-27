package ru.stolexiy.catman.ui.common

import android.os.Bundle
import android.os.Parcel
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson

fun <T> Bundle.setJson(serializer: Gson, key: String, value: T) {
    putString(key, serializer.toJson(value))
}

inline fun <reified T> Bundle.getJson(serializer: Gson, key: String): T? {
    return serializer.fromJson(getString(key), T::class.java)
}

fun <T> SavedStateHandle.setJson(serializer: Gson, key: String, value: T) {
    set(key, serializer.toJson(value))
}

inline fun <reified T> SavedStateHandle.getJson(serializer: Gson, key: String): T? {
    return serializer.fromJson(get<String>(key), T::class.java)
}

inline fun <reified T> SavedStateHandle.removeJson(serializer: Gson, key: String): T? {
    return remove<String>(key)?.let {
        serializer.fromJson(it, T::class.java)
    }
}

fun SavedStateHandle.removeJson(key: String) {
    remove<String>(key)
}

fun Parcel.writeBooleanCompat(boolean: Boolean) {
    writeByte(if (boolean) 1.toByte() else 0.toByte())
}

fun Parcel.readBooleanCompat() = readByte() != 0.toByte()
