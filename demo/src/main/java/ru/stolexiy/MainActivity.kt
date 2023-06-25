package ru.stolexiy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.stolexiy.demo.R
import ru.stolexiy.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
