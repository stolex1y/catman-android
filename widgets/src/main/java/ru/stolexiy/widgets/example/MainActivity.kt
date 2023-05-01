package ru.stolexiy.widgets.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.stolexiy.widgets.databinding.ActivityMainBinding
import ru.stolexiy.widgets.example.progressview.SimpleActivityWithProgressView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            circleProgress.setOnClickListener {
                val intent = Intent(this@MainActivity, SimpleActivityWithProgressView::class.java)
                startActivity(intent)
            }
        }
    }
}