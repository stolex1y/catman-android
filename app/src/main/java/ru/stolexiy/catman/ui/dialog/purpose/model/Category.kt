package ru.stolexiy.catman.ui.dialog.purpose.model

import ru.stolexiy.catman.domain.model.DomainCategory
import java.io.Serializable

data class Category(
    val id: Long,
    val color: Int,
    val name: String,
) : Serializable {
}

fun DomainCategory.toCategory() = Category(id, color, name)