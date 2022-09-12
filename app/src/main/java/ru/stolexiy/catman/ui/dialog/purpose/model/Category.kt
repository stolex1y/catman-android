package ru.stolexiy.catman.ui.dialog.purpose.model

import ru.stolexiy.catman.domain.model.DomainCategory

data class Category(
    val id: Long,
    val color: Int,
    val name: String,
) {
}

fun DomainCategory.toCategory() = Category(id, color, name)