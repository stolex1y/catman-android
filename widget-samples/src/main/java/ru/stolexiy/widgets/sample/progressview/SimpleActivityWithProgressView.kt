package ru.stolexiy.widgets.sample.progressview

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import ru.stolexiy.widgets.ProgressView
import ru.stolexiy.widgets.sample.R
import ru.stolexiy.widgets.sample.databinding.ProgressViewAcitivtyBinding
import kotlin.math.roundToInt

internal class SimpleActivityWithProgressView : AppCompatActivity() {

    companion object {
        @VisibleForTesting
        const val MAX_COUNT = 10
    }

    private lateinit var binding: ProgressViewAcitivtyBinding
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProgressViewAcitivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            buttonFillingProgress.setOnClickListener {
                progressView.fillingUp = progressView.fillingUp.not()
                buttonFillingProgress.text =
                    if (progressView.fillingUp)
                        getString(R.string.filling)
                    else
                        getString(R.string.decreasing)
            }

            buttonClockwise.setOnClickListener {
                progressView.clockwise = progressView.clockwise.not()
                buttonClockwise.text =
                    if (progressView.clockwise)
                        getString(R.string.clockwise)
                    else
                        getString(R.string.counterclockwise)
            }

            buttonCountUp.setOnClickListener {
                count = (count + 1) % MAX_COUNT
                updateProgress()
            }

            buttonCountDown.setOnClickListener {
                count = if (count - 1 < 0)
                    MAX_COUNT - 1
                else
                    count - 1
                updateProgress()
            }
        }
        updateProgress()
    }

    private fun updateProgress() {
        val progress = count / MAX_COUNT.toFloat()
        binding.progressView.apply {
            this.progress = progress
            this.textCalculator = ProgressView.TextCalculator {
                getTextFromProgress(it)
            }
        }
    }

    private fun getTextFromProgress(progress: Float): String {
        return (progress * MAX_COUNT).roundToInt().toString()
    }
}