package ru.stolexiy.catman.ui.dialog.category.model

import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import ru.stolexiy.catman.domain.model.DomainColor

data class Color(
    @ColorInt val argb: Int,
    @StringRes val name: Int
) {

    companion object {
        fun fromDomainColor(domainColor: DomainColor) = Color(
            argb = domainColor.argb,
            name = domainColor.name
        )
    }
}
