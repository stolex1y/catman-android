package ru.stolexiy.catman.ui.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import ru.stolexiy.catman.R

object NotificationChannels {
    const val BACKGROUND_WORK = "BACKGROUND"
    const val TIMER = "TIMER"

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun NotificationManager.initChannels(appContext: Context) {
        val channels: MutableList<NotificationChannel> = mutableListOf()
        channels += NotificationChannel(
            BACKGROUND_WORK,
            appContext.getString(R.string.background_work),
            NotificationManager.IMPORTANCE_MIN
        )
        channels += NotificationChannel(
            TIMER,
            appContext.getString(R.string.timer),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        createNotificationChannels(channels)
    }
}
