package ru.stolexiy.catman.ui.util.work

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.workDataOf
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.notification.NotificationChannels
import ru.stolexiy.common.Json
import kotlin.reflect.KClass

object WorkUtils {
    const val ADD_PURPOSE_NOTIFICATION_ID = 1
    const val UPDATE_PURPOSE_NOTIFICATION_ID = 2
    const val DELETE_PURPOSE_NOTIFICATION_ID = 3
    const val ADD_CATEGORY_NOTIFICATION_ID = 4
    const val UPDATE_CATEGORY_NOTIFICATION_ID = 5
    const val DELETE_CATEGORY_NOTIFICATION_ID = 6

    private const val OBJECT_DATA = "OBJECT_DATA"
    private const val PRIMITIVE_DATA = "PRIMITIVE_DATA"

    fun createNotificationBackgroundWork(message: String, context: Context): Notification {
        return NotificationCompat.Builder(context, NotificationChannels.BACKGROUND_WORK)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSmallIcon(R.drawable.settings)
            .setContentTitle(context.getString(R.string.background_work))
            .setContentText(message)
            .build()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> Data.deserialize(clazz: KClass<T>): T? {
        require(
            this.keyValueMap.containsKey(OBJECT_DATA) ||
                    this.keyValueMap.containsKey(PRIMITIVE_DATA)
        ) {
            "Invalid data: it doesn't contain known data of class ${clazz.simpleName}"
        }
        return if (this.keyValueMap.containsKey(PRIMITIVE_DATA)) {
            this.keyValueMap[PRIMITIVE_DATA] as T
        } else {
            val serialized = keyValueMap[OBJECT_DATA] as String
            Json.serializer.fromJson(serialized, clazz.java)
        }
    }

    fun <T> serialize(data: T): Data {
        return if (data.needSerializing()) {
            val serialized = Json.serializer.toJson(data)
            workDataOf(OBJECT_DATA to serialized)
        } else {
            workDataOf(PRIMITIVE_DATA to data)
        }
    }

    private fun <T> T.needSerializing(): Boolean {
        return when (this) {
            null -> true
            is String, is Int, is Long, is Double, is Float, is Char, is Boolean -> false
            else -> true
        }
    }
}
