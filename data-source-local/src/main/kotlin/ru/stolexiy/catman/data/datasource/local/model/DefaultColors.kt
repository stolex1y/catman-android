package ru.stolexiy.catman.data.datasource.local.model

import androidx.annotation.StringRes
import ru.stolexiy.catman.data.datasource.local.R

enum class DefaultColors(val rgba: Int, @StringRes val nameRes: Int) {
    CREAM(0xFDF4E3, R.string.cream),
    AQUAMARINE(0x7FFFD4, R.string.aquamarine),
    AMARANTH_PINK(0xF19CBB, R.string.amaranth_pink)
}
