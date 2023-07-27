package ru.stolexiy.common

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import ru.stolexiy.common.timer.Time
import java.lang.reflect.Type

class TimeSerializer : JsonSerializer<Time> {
    override fun serialize(
        src: Time?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return src?.let {
            JsonPrimitive(it.inMs)
        } ?: JsonNull.INSTANCE
    }
}

class TimeDeserializer : JsonDeserializer<Time> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Time? {
        return json?.let {
            if (it.isJsonNull)
                null
            else
                Time.from(it.asLong)
        }
    }
}
