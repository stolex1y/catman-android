package ru.stolexiy.catman.core.di

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.stolexiy.catman.core.OptionalExtensions.toOptional
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
    }
}
