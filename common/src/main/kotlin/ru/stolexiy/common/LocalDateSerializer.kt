package ru.stolexiy.common

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import ru.stolexiy.common.DateUtils.toEpochMillis
import ru.stolexiy.common.DateUtils.toLocalDate
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.ZoneOffset

class LocalDateSerializer : JsonSerializer<LocalDate> {
    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return src?.let {
            JsonPrimitive(it.toEpochMillis(ZoneOffset.UTC))
        } ?: JsonNull.INSTANCE
    }
}

class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        return json?.let {
            if (it.isJsonNull)
                null
            else
                it.asLong.toLocalDate(ZoneOffset.UTC)
        }
    }
}
