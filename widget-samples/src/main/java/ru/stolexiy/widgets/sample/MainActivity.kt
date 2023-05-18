package ru.stolexiy.widgets.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.stolexiy.widgets.sample.databinding.ActivityMainBinding
import ru.stolexiy.widgets.sample.progressview.SimpleActivityWithProgressView
import ru.stolexiy.widgets.sample.timerview.SimpleActivityWithTimerView
import kotlin.reflect.KClass

internal class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            circleProgress.setOnClickListener {
                startActivityViaIntent(SimpleActivityWithProgressView::class)
            }
            timerView.setOnClickListener {
                startActivityViaIntent(SimpleActivityWithTimerView::class)
            }
        }
    }

    private fun <T : Activity> startActivityViaIntent(activity: KClass<T>) {
        val intent = Intent(this, activity.java)
        startActivity(intent)
    }
}