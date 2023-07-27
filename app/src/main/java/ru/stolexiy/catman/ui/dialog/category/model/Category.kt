package ru.stolexiy.catman.ui.dialog.category.model

import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainColor
import ru.stolexiy.catman.ui.util.DefaultConditions
import ru.stolexiy.catman.ui.util.validation.ValidatedEntity
import java.io.Serializable

class Category(
    name: String = "",
    color: Color? = null,
    val id: Long = 0,
    var description: String = ""
) : Serializable, ValidatedEntity() {
    val name: ValidatedProperty<String> =
        validatedProperty(name, DefaultConditions.notEmpty() * DefaultConditions.notNull())
    val color: ValidatedProperty<Color?> =
        validatedProperty(color, DefaultConditions.notNull())

    constructor() : this(
        name = "",
        color = null,
        id = 0,
        description = ""
    )

    fun toDomainCategory(): DomainCategory {
        return DomainCategory(
            name = name.get(),
            color = color.get()!!.argb,
            id = id,
            description = description
        )
    }

    companion object {
        fun fromDomainCategory(
            domainCategory: DomainCategory,
            domainColor: DomainColor
        ) = Category(
            name = domainCategory.name,
            color = Color(domainColor.argb, domainColor.name),
            id = domainCategory.id,
            description = domainCategory.description
        )
    }
}
