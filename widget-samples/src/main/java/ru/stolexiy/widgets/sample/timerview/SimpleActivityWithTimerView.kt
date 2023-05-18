package ru.stolexiy.widgets.sample.timerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.stolexiy.common.Timer
import ru.stolexiy.widgets.sample.R
import ru.stolexiy.widgets.sample.databinding.TimerViewActivityBinding

internal class SimpleActivityWithTimerView : AppCompatActivity() {

    private lateinit var binding: TimerViewActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimerViewActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            buttonStart.setOnClickListener {
                when (timerView.timerState()) {
                    Timer.State.STOPPED, Timer.State.PAUSED -> {
                        timerView.timerStart()
                        buttonStart.text = getString(R.string.pause)
                    }

                    Timer.State.RUNNING -> {
                        timerView.timerPause()
                        buttonStart.text = getString(R.string.start)
                    }
                }
            }
            buttonReset.setOnClickListener {
                timerView.timerReset()
            }
        }
    }
}