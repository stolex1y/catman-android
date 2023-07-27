package ru.stolexiy.catman.ui.details.purpose.model

import ru.stolexiy.catman.domain.model.DomainCategory

data class Category(
    val id: Long,
    val color: Int,
    val name: String,
) {
    constructor() : this(
        id = 0,
        color = 0,
        name = "",
    )
}

fun DomainCategory.toCategory() =
    Category(
        id = id,
        color = color,
        name = name
    )
