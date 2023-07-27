package ru.stolexiy.catman.ui.util

import ru.stolexiy.widgets.ProgressView
import kotlin.math.roundToInt

object TextCalculators {
    @JvmField
    val PERCENT: ProgressView.TextCalculator =
        ProgressView.TextCalculator { progress -> "${(progress * 100).roundToInt()}%" }
}
