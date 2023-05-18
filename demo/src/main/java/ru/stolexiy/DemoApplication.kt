package ru.stolexiy

import android.app.Application
import android.os.Process
import ru.stolexiy.demo.BuildConfig
import timber.log.Timber

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(
                        priority, "[$GLOBAL_TAG]" +
                                "$tag | ${Process.getElapsedCpuTime()} ms", message, t
                    )
                }
            })
    }

    companion object {
        private const val GLOBAL_TAG: String = "AY"
    }
}