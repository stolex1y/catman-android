package ru.stolexiy.catman.core

import com.google.gson.GsonBuilder
import java.util.Calendar

object Json {
    val serializer by lazy {
        GsonBuilder()
            .registerTypeHierarchyAdapter(Calendar::class.java, CalendarSerializer())
            .registerTypeHierarchyAdapter(Calendar::class.java, CalendarDeserializer())
            .create()
    }
}
