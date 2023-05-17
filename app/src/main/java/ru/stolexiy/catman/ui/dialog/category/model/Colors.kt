package ru.stolexiy.catman.ui.dialog.category.model

import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.model.DefaultColor

object Colors {
    val defaultColorNames = mapOf(
        DefaultColor.CREAM.rgba.let { it to R.string.cream },
        DefaultColor.AQUAMARINE.rgba.let { it to R.string.aquamarine },
        DefaultColor.AMARANTH_PINK.rgba.let { it to R.string.amaranth_pink }
    )
}
