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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.MainActivity
import ru.stolexiy.catman.ui.util.notification.NotificationChannels
import ru.stolexiy.catman.ui.util.notification.NotificationUtils
import ru.stolexiy.common.DateUtils.toMinSecFormat
import ru.stolexiy.common.di.CoroutineDispatcherNames
import ru.stolexiy.common.timer.Time
import ru.stolexiy.common.timer.MutableTime
import ru.stolexiy.common.timer.TimeConstants
import ru.stolexiy.common.timer.Timer
import timber.log.Timber
import java.util.Optional
import javax.inject.Inject
import javax.inject.Named
import kotlin.jvm.optionals.getOrNull

@AndroidEntryPoint
class TimerService : Service() {

    companion object {
        private val ACTION_PREFIX = TimerService::class.java.name
        private val ACTION_SERVICE_STOP = "$ACTION_PREFIX.SERVICE_STOP"

        private const val NOTIFICATION_UPDATE_TIME: Long = 900
        private const val TIMER_INIT_TIME: Long = TimeConstants.SEC_TO_MS * 10
    }

    private val notificationLayout by lazy {
        RemoteViews(packageName, R.layout.notification_timer)
    }

    private val notificationId = NotificationUtils.getUniqueNotificationId()
    private val notificationBuilder by lazy {
        NotificationCompat.Builder(this, NotificationChannels.TIMER)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(false)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setDeleteIntent(intentWithStopServiceAction())
            .setOnlyAlertOnce(true)
            .setContentIntent()
    }

    @Inject
    lateinit var timer: Timer

    @Inject
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER)
    lateinit var defaultDispatcher: CoroutineDispatcher

    @Inject
    lateinit var notificationManager: Optional<NotificationManager>

    @Inject
    lateinit var vibrator: Optional<Vibrator>

    private val binder: Binder by lazy {
        Binder(timer)
    }

    private val coroutineScope by lazy {
        CoroutineScope(defaultDispatcher)
    }

    private val timerListener = TimerListener()

    private val timerActionReceiver = TimerActionReceiver()
    private val stopServiceReceiver = StopServiceReceiver()

    private var started: Boolean = false

    private val pauseAction: PendingIntent by lazy {
        intentWithTimerAction(TimerCommand.PAUSE)
    }

    private val startAction: PendingIntent by lazy {
        intentWithTimerAction(TimerCommand.RESUME)
    }

    private val stopAction: PendingIntent by lazy {
        intentWithTimerAction(TimerCommand.STOP)
    }

    override fun onCreate() {
        super.onCreate()
        timer.apply {
            initTime = MutableTime(TIMER_INIT_TIME)
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
            return START_NOT_STICKY

        Timber.d("start service")
        startForeground(notificationId, notificationBuilder.build())
        registerReceivers()
        timer.reset()
        started = true
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("unbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Timber.d("destroy")
        timer.stop()
        timer.clearListeners()
        coroutineScope.cancel()
        cancelNotification()
        unregisterReceivers()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.d("onTaskRemoved")
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    private fun intentWithTimerAction(command: TimerCommand): PendingIntent {
        return PendingIntent.getBroadcast(
            this,
            0,
            Intent(command.codeName),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun intentWithStopServiceAction(): PendingIntent {
        return PendingIntent.getBroadcast(
            this,
            0,
            Intent(ACTION_SERVICE_STOP),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateNotificationTimeText(time: Time) {
        notificationLayout.setTextViewText(R.id.text_time, time.toMinSecFormat())
    }

    private fun cancelNotification() {
        notificationManager.getOrNull()?.cancel(notificationId)
    }

    private fun toggleOngoing(enable: Boolean) {
        notificationBuilder.setOngoing(enable)
    }

    private fun updateNotification() {
        notificationManager.getOrNull()?.notify(notificationId, notificationBuilder.build())
    }

    private fun registerReceivers() {
        val timerActionIntentFilter = IntentFilter().apply {
            TimerCommand.values().forEach {
                addAction(it.codeName)
            }
        }
        ContextCompat.registerReceiver(
            this,
            timerActionReceiver,
            timerActionIntentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        val stopServiceIntentFilter = IntentFilter(ACTION_SERVICE_STOP)
        ContextCompat.registerReceiver(
            this,
            stopServiceReceiver,
            stopServiceIntentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun unregisterReceivers() {
        unregisterReceiver(timerActionReceiver)
        unregisterReceiver(stopServiceReceiver)
    }

    private fun setUpToggleStartButton(isGone: Boolean, checked: Boolean) {
        setUpToggleStartButtonListener(isGone, checked)

        val srcId = if (isGone)
            R.color.transparent
        else if (checked)
            R.drawable.pause
        else
            R.drawable.play

        notificationLayout.setImageViewResource(R.id.toggle_button_start, srcId)
    }

    private fun setUpToggleStopButton(checked: Boolean) {
        setUpToggleStopButtonListener(checked)

        val srcId = if (checked)
            R.drawable.play
        else
            R.drawable.stop

        notificationLayout.setImageViewResource(R.id.toggle_button_stop, srcId)
    }

    private fun setUpToggleStopButtonListener(checked: Boolean) {
        val actionIntent = if (checked)
            startAction
        else
            stopAction

        notificationLayout.setOnClickPendingIntent(
            R.id.toggle_button_stop,
            actionIntent
        )
    }

    private fun setUpToggleStartButtonListener(isGone: Boolean, checked: Boolean) {
        val actionIntent = if (isGone)
            null
        else if (checked)
            pauseAction
        else
            startAction

        notificationLayout.setOnClickPendingIntent(
            R.id.toggle_button_start,
            actionIntent
        )
    }

    private fun NotificationCompat.Builder.setContentIntent(): NotificationCompat.Builder {
        return setContentIntent(
            PendingIntent.getActivity(
                this@TimerService,
                notificationId,
                Intent(this@TimerService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra(MainActivity.START_FRAGMENT_EXTRA_KEY, R.id.timer_fragment)
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    class Binder(val timer: Timer) : android.os.Binder()

    private inner class TimerListener : Timer.Listener {
        override val updateTime: Long
            get() = NOTIFICATION_UPDATE_TIME

        override fun onUpdateTime(timer: Timer) {
            updateNotificationTimeText(timer.curTime)
            updateNotification()
        }

        override fun onStart(timer: Timer) {
            setUpToggleStopButton(checked = false)
            setUpToggleStartButton(isGone = false, checked = true)
            toggleOngoing(enable = true)
            updateNotification()
        }

        override fun onPause(timer: Timer) {
            setUpToggleStopButton(checked = false)
            setUpToggleStartButton(isGone = false, checked = false)
            updateNotification()
        }

        override fun onFinish(timer: Timer) {
            setUpToggleStopButton(checked = true)
            setUpToggleStartButton(isGone = true, checked = false)
            toggleOngoing(enable = false)
            updateNotification()
            vibrator.getOrNull()?.let { vibrator ->
                vibrator.vibrate(200)
            }
            timer.reset()
        }

        override fun onStop(timer: Timer) {
            toggleOngoing(enable = false)
            setUpToggleStopButton(checked = true)
            setUpToggleStartButton(isGone = true, checked = false)
            updateNotification()
        }

    }

    private inner class TimerActionReceiver : BroadcastReceiver() {
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

    private inner class StopServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            this@TimerService.stopSelf()
        }
    }

    private enum class TimerCommand(val codeName: String) {
        STOP("$ACTION_PREFIX.TIMER_STOP"),
        RESUME("$ACTION_PREFIX.TIMER_RESUME"),
        PAUSE("$ACTION_PREFIX.TIMER_PAUSE");

        companion object {
            fun parse(codeName: String?): TimerCommand? {
                return values().find { it.codeName == codeName }
            }
        }
    }
}
