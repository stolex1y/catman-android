package ru.stolexiy.catman.ui.util.work

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.notification.NotificationChannels

object WorkUtils {
    const val ADD_PURPOSE_NOTIFICATION_ID = 1
    const val UPDATE_PURPOSE_NOTIFICATION_ID = 2
    const val DELETE_PURPOSE_NOTIFICATION_ID = 3
    fun createNotificationBackgroundWork(message: String, context: Context): Notification {
        return NotificationCompat.Builder(context, NotificationChannels.BACKGROUND_WORK)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSmallIcon(R.drawable.settings)
            .setContentTitle(context.getString(R.string.background_work))
            .setContentText(message)
            .build()
    }
}
