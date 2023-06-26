package ru.stolexiy.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.ZonedDateTime

object Json {
    val serializer: Gson by lazy {
        GsonBuilder()
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, ZonedDateTimeSerializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, ZonedDateTimeDeserializer())
            .create()
    }
}
