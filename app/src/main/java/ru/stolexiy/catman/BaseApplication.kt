package ru.stolexiy.catman

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.stolexiy.catman.ui.util.di.entryPointApplicationAccessor
import timber.log.Timber

open class BaseApplication : Application(), Configuration.Provider {
    private val entryPoint by entryPointApplicationAccessor<BaseApplicationEntryPoint>(
        ::getApplicationContext
    )

    private val workerFactory: HiltWorkerFactory by lazy {
        entryPoint.workerFactory()
    }

    override fun onCreate() {
        super.onCreate()
        Thread.currentThread().setUncaughtExceptionHandler { _, error ->
            Timber.e(error, "Uncaught exception:")
        }
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BaseApplicationEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }
}
