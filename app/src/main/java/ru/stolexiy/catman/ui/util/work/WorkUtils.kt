package ru.stolexiy.catman.ui.util.work

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.workDataOf
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.notification.NotificationChannels

object WorkUtils {
    const val ADD_PURPOSE_NOTIFICATION_ID = 1
    const val UPDATE_PURPOSE_NOTIFICATION_ID = 2
    const val DELETE_PURPOSE_NOTIFICATION_ID = 3

    private const val OUTPUT_ERROR = "OUTPUT_ERROR"
    private const val OUTPUT_RESULT = "OUTPUT_RESULT"

    fun createNotificationBackgroundWork(message: String, context: Context): Notification {
        return NotificationCompat.Builder(context, NotificationChannels.BACKGROUND_WORK)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSmallIcon(R.drawable.settings)
            .setContentTitle(context.getString(R.string.background_work))
            .setContentText(message)
            .build()
    }

    fun Data.getThrowable(): Throwable {
        require(this.keyValueMap.containsKey(OUTPUT_ERROR)) {
            "Invalid data: it doesn't contain error"
        }
        return this.keyValueMap[OUTPUT_ERROR] as Throwable
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> Data.getResult(): T {
        require(this.keyValueMap.containsKey(OUTPUT_RESULT)) {
            "Invalid data: it doesn't contain result"
        }
        val result = this.keyValueMap[OUTPUT_RESULT]
        try {
            return result as T
        } catch (e: ClassCastException) {
            throw IllegalArgumentException("Invalid data: it contains an unexpected type")
        }
    }

    fun <T> toOutputData(data: T): Data {
        return workDataOf(OUTPUT_RESULT to data)
    }

    fun toOutputError(t: Throwable): Data {
        return workDataOf(OUTPUT_ERROR to t)
    }
}
