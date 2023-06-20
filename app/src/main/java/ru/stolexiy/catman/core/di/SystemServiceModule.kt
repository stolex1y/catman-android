package ru.stolexiy.catman.core.di

import android.app.NotificationManager
import android.content.Context
import android.os.Vibrator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.stolexiy.common.OptionalExtensions.toOptional
import java.util.Optional

@Module
@InstallIn(SingletonComponent::class)
interface SystemServiceModule {

    companion object {
        @Provides
        fun notificationManager(
            @ApplicationContext context: Context
        ): Optional<NotificationManager> {
            val notificationManager =
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)
            return notificationManager.toOptional()
        }

        @Provides
        fun vibrator(
            @ApplicationContext context: Context
        ): Optional<Vibrator> {
            val notificationManager =
                (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?)
            return notificationManager.toOptional()
        }
    }
}
