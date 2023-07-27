package ru.stolexiy.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.ZonedDateTime

object Json {
    val serializer: Gson by lazy {
        GsonBuilder()
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, ZonedDateTimeSerializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, ZonedDateTimeDeserializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, LocalDateSerializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, LocalDateDeserializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, LocalTimeSerializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, LocalTimeDeserializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, TimeSerializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, TimeDeserializer())
            .create()
    }
}
