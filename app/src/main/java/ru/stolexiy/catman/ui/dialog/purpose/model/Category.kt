package ru.stolexiy.catman.ui.dialog.purpose.model

import org.jetbrains.annotations.VisibleForTesting
import ru.stolexiy.catman.domain.model.DomainCategory
import java.io.Serializable

data class Category(
    val id: Long,
    val color: Int,
    val name: String,
) : Serializable {
    @VisibleForTesting
    fun toDomainCategory() = DomainCategory(name = name, color = color, id = id)

    companion object {
        fun fromDomainCategory(category: DomainCategory) =
            Category(
                id = category.id,
                color = category.color,
                name = category.name
            )
    }
}
