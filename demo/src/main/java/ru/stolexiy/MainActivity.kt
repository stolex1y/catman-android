package ru.stolexiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.stolexiy.common.Timer
import ru.stolexiy.demo.R
import ru.stolexiy.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            buttonStart.setOnClickListener {
                when (timerView.timerState()) {
                    Timer.State.STOPPED, Timer.State.PAUSED -> {
                        timerView.timerStart()
                        buttonStart.text = "Пауза"
                    }

                    Timer.State.RUNNING -> {
                        timerView.timerPause()
                        buttonStart.text = "Старт"
                    }
                }
            }
            buttonStop.setOnClickListener {
                timerView.timerReset()
            }
        }
    }
}