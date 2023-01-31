package ru.stolexiy.catman.ui.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import ru.stolexiy.catman.R

object NotificationChannels {
    val BACKGROUND_WORK = "BACKGROUND"

    private val mChannels = mutableMapOf<String, NotificationChannel>()
    val channels: Map<String, NotificationChannel>
        get() = mChannels

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun initChannels(context: Context) {
        mChannels += BACKGROUND_WORK to NotificationChannel(
            BACKGROUND_WORK,
            context.getString(R.string.background_work),
            NotificationManager.IMPORTANCE_MIN
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannels(channels.values.toList())
    }
}