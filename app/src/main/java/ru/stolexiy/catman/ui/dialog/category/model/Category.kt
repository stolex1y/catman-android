package ru.stolexiy.catman.ui.dialog.category.model

import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.ui.util.DefaultConditions
import ru.stolexiy.catman.ui.util.validation.Conditions
import ru.stolexiy.catman.ui.util.validation.ValidatedEntity
import java.io.Serializable

class Category(
    name: String = "",
    color: Color? = null,
    id: Long = 0,
    description: String? = null
) : Serializable, ValidatedEntity() {
    var name: ValidatedProperty<String> = validatedProperty(name, DefaultConditions.notEmpty())
    var color: ValidatedProperty<Color?> = validatedProperty(color, DefaultConditions.notNull())
    val id: ValidatedProperty<Long> = validatedProperty(id, Conditions.None())
    var description: ValidatedProperty<String?> = validatedProperty(description, DefaultConditions.notEmpty())

    fun toDomainCategory() = DomainCategory(
        name = name.get(),
        color = color.get()?.color ?: throw IllegalStateException(),
        id = id.get(),
        description = description.get()
    )

    companion object {
        fun fromDomainCategory(domainCategory: DomainCategory) = Category(
            name = domainCategory.name,
            color = C
        )
    }
}
