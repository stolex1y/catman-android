package ru.stolexiy.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.stolexiy.common.timer.Time
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

object Json {
    val serializer: Gson by lazy {
        GsonBuilder()
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, ZonedDateTimeSerializer())
            .registerTypeHierarchyAdapter(ZonedDateTime::class.java, ZonedDateTimeDeserializer())
            .registerTypeHierarchyAdapter(LocalDate::class.java, LocalDateSerializer())
            .registerTypeHierarchyAdapter(LocalDate::class.java, LocalDateDeserializer())
            .registerTypeHierarchyAdapter(LocalTime::class.java, LocalTimeSerializer())
            .registerTypeHierarchyAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .registerTypeHierarchyAdapter(Time::class.java, TimeSerializer())
            .registerTypeHierarchyAdapter(Time::class.java, TimeDeserializer())
            .create()
    }
}
