package ru.stolexiy.catman.ui.dialog.category.model

import android.content.Context
import ru.stolexiy.catman.domain.model.DomainColor

data class Color(
    val color: Int,
    val name: String
) {

    companion object {
        fun fromDomainColor(context: Context, domainColor: DomainColor) = Color(
            color = domainColor.color,
            name = context.getString(domainColor.name)
        )

        fun fromUnknownColor(color: Int) = Color(
            color = color,
            name = "#${String.format("#%06x", color)}"
        )
    }
}
