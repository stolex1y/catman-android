package ru.stolexiy.widgets.sample.timerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import ru.stolexiy.common.timer.Timer
import ru.stolexiy.widgets.sample.R
import ru.stolexiy.widgets.sample.databinding.TimerViewActivityBinding

internal class SimpleActivityWithTimerView : AppCompatActivity() {

    private lateinit var binding: TimerViewActivityBinding

    @OptIn(DelicateCoroutinesApi::class)
    private val timer: Timer = Timer { newSingleThreadContext(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimerViewActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            timerView.attachTimer(timer)
            buttonStart.setOnClickListener {
                when (timer.state) {
                    Timer.State.STOPPED, Timer.State.PAUSED -> {
                        timer.start()
                        buttonStart.text = getString(R.string.pause)
                    }

                    Timer.State.RUNNING -> {
                        timer.pause()
                        buttonStart.text = getString(R.string.start)
                    }

                    else -> { /* no-op */
                    }
                }
            }
            buttonReset.setOnClickListener {
                timer.reset()
            }
        }
    }
}
