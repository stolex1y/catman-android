package ru.stolexiy.catman.data.datasource.local.model

import androidx.annotation.StringRes
import ru.stolexiy.catman.data.datasource.local.R

enum class DefaultColors(val argb: Int, @StringRes val nameRes: Int) {
    CREAM(0xFFFDF4E3.toInt(), R.string.cream),
    AQUAMARINE(0xFF7FFFD4.toInt(), R.string.aquamarine),
    AMARANTH_PINK(0xFFF19CBB.toInt(), R.string.amaranth_pink)
}
