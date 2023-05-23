package ru.stolexiy.catman.ui.dialog.category.model

import android.content.Context
import ru.stolexiy.catman.core.model.DefaultColors
import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainColor
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
    var description: ValidatedProperty<String?> = validatedProperty(description, Conditions.None())

    fun toDomainCategory() = DomainCategory(
        name = name.get(),
        color = color.get()?.color ?: throw IllegalStateException(),
        id = id.get(),
        description = description.get()
    )

    companion object {
        fun fromDomainCategory(
            context: Context,
            domainCategory: DomainCategory,
            domainColor: DomainColor? = null
        ): Category {
            val rgba = domainCategory.color
            val color = if (domainColor != null) {
                Color.fromDomainColor(context, domainColor)
            } else if (DefaultColors.values().map { it.rgba }.contains(rgba)) {
                Color(
                    rgba,
                    context.getString(DefaultColors.values().find { it.rgba == rgba }!!.nameRes)
                )
            } else {
                Color.fromUnknownColor(rgba)
            }
            return Category(
                name = domainCategory.name,
                color = color,
                id = domainCategory.id,
                description = domainCategory.description
            )
        }
    }
}
