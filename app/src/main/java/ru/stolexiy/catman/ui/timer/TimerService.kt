package ru.stolexiy.catman.ui.timer

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.notification.NotificationChannels
import ru.stolexiy.catman.ui.util.notification.NotificationUtils
import ru.stolexiy.common.TimeConstants
import ru.stolexiy.common.Timer
import ru.stolexiy.common.di.CoroutineDispatcherNames
import timber.log.Timber
import java.util.Optional
import javax.inject.Inject
import javax.inject.Named
import kotlin.jvm.optionals.getOrNull

private const val NOTIFICATION_UPDATE_TIME: Long = 1000
private const val TIMER_INIT_TIME: Long = TimeConstants.SEC_TO_MS * 10

@AndroidEntryPoint
class TimerService : Service() {

    private val notificationId = NotificationUtils.getUniqueNotificationId()
    private val notificationBuilder by lazy {
        NotificationCompat.Builder(this, NotificationChannels.TIMER)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .addAction(
                R.drawable.pause,
                getString(R.string.pause),
                intentWithAction(TimerCommand.PAUSE)
            )
            .addAction(
                R.drawable.play,
                getString(R.string.start),
                intentWithAction(TimerCommand.RESUME)
            )
            .addAction(
                R.drawable.settings,
                getString(R.string.finish),
                intentWithAction(TimerCommand.STOP)
            )
    }

    @Inject
    lateinit var timer: Timer

    @Inject
    @Named(CoroutineDispatcherNames.IO_DISPATCHER)
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var notificationManager: Optional<NotificationManager>

    @Inject
    lateinit var vibrator: Optional<Vibrator>

    private val binder: Binder by lazy {
        Binder(timer)
    }

    private val coroutineScope by lazy {
        CoroutineScope(ioDispatcher)
    }

    private val timerListener = TimerListener()

    private val broadcastReceiver = Receiver()

    private var started: Boolean = false

    override fun onCreate() {
        super.onCreate()
        timer.apply {
            initTime = Timer.Time(TIMER_INIT_TIME)
            addListener(timerListener)
        }
        Timber.d("create")
    }

    override fun onBind(intent: Intent?): IBinder {
        Timber.d("bind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("received start command")
        if (started)
            return START_STICKY

        Timber.d("start service")
        startForeground(notificationId, notificationBuilder.build())
        registerReceiver()
        timer.reset()
        started = true
        return START_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("unbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Timber.d("destroy")
        timer.stop()
        timer.removeListener(timerListener)
        coroutineScope.cancel()
        cancelNotification()
        unregisterReceiver()
        super.onDestroy()
    }

    private fun intentWithAction(command: TimerCommand): PendingIntent {
        return PendingIntent.getBroadcast(
            this,
            0,
            Intent().apply {
                action = command.codeName
            },
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateNotification(time: Timer.ImmutableTime) {
        notificationManager.getOrNull()?.notify(
            notificationId,
            notificationBuilder.setContentText("%02d:%02d".format(time.min, time.sec))
                .build()
        )
    }

    private fun cancelNotification() {
        notificationManager.getOrNull()?.cancel(notificationId)
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            TimerCommand.values().forEach {
                addAction(it.codeName)
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterReceiver() {
        unregisterReceiver(broadcastReceiver)
    }

    data class Binder(val timer: Timer) : android.os.Binder()

    private inner class TimerListener : Timer.Listener {
        override val updateTime: Long
            get() = NOTIFICATION_UPDATE_TIME

        override fun onUpdateTime(timer: Timer) {
            updateNotification(timer.curTime)
        }

        override fun onFinish(timer: Timer) {
            vibrator.getOrNull()?.let { vibrator ->
                vibrator.vibrate(200)
            }
        }
    }

    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val codeName = intent.action
            when (TimerCommand.parse(codeName)) {
                TimerCommand.STOP -> timer.stop()
                TimerCommand.RESUME -> timer.start()
                TimerCommand.PAUSE -> timer.pause()
                null -> {
                    Timber.w("received null or unknown command: $codeName")
                }
            }
        }
    }

    private enum class TimerCommand(val codeName: String) {
        STOP("ru.stolexiy.catman.timer.STOP"),
        RESUME("ru.stolexiy.catman.timer.RESUME"),
        PAUSE("ru.stolexiy.catman.timer.PAUSE");

        companion object {
            fun parse(codeName: String?): TimerCommand? {
                return values().find { it.codeName == codeName }
            }
        }
    }
}