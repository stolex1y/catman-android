package ru.stolexiy.common

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import ru.stolexiy.common.DateUtils.toEpochMillis
import ru.stolexiy.common.DateUtils.toLocalTime
import java.lang.reflect.Type
import java.time.LocalTime

class LocalTimeSerializer : JsonSerializer<LocalTime> {
    override fun serialize(
        src: LocalTime?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return src?.let {
            JsonPrimitive(it.toEpochMillis())
        } ?: JsonNull.INSTANCE
    }
}

class LocalTimeDeserializer : JsonDeserializer<LocalTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalTime? {
        return json?.let {
            if (it.isJsonNull)
                null
            else
                it.asLong.toLocalTime()
        }
    }
}
