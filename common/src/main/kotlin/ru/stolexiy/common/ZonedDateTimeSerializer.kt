package ru.stolexiy.common

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import ru.stolexiy.common.DateUtils.toEpochMillis
import ru.stolexiy.common.DateUtils.toZonedDateTime
import java.lang.reflect.Type
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeSerializer : JsonSerializer<ZonedDateTime> {
    override fun serialize(
        src: ZonedDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonArray(2).apply {
            add(src?.zone?.id)
            add(src?.toEpochMillis())
        }
    }
}

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ZonedDateTime? {
        return json?.asJsonArray?.run {
            if (get(0).isJsonNull)
                return@run null

            val zone = ZoneId.of(get(0).asJsonPrimitive.asString)
            val millis = get(1).asJsonPrimitive.asLong
            (millis to zone).toZonedDateTime()
        }
    }
}
