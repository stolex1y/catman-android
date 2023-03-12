package ru.stolexiy.catman.ui.dialog.purpose.model

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.ui.util.Converters
import ru.stolexiy.catman.ui.util.DefaultConditions
import ru.stolexiy.catman.ui.util.validation.Conditions
import ru.stolexiy.catman.ui.util.validation.ValidatedEntity
import java.io.Serializable
import java.util.Calendar

class Purpose(
    name: String = "",
    category: Category? = null,
    deadline: Calendar? = null,
    description: String? = null,
    purposeId: Long = 0
) : Serializable, ValidatedEntity() {
    var name: ValidatedProperty<String> = validatedProperty(name, DefaultConditions.notEmpty())
    var category: ValidatedProperty<Category?> = validatedProperty(category, DefaultConditions.notNull())
    var deadline: ValidatedProperty<Calendar?> = validatedProperty(deadline, DefaultConditions.fromToday())
    var description: ValidatedProperty<String?> = validatedProperty(description, Conditions.None())
    var purposeId: ValidatedProperty<Long> = validatedProperty(purposeId, Conditions.None())

    fun toDomainPurpose() = DomainPurpose(
        name = name.get(),
        categoryId = category.get()?.id ?: throw IllegalStateException("Is required field"),
        deadline = deadline.get() ?: throw IllegalStateException("Is required field"),
        description = description.get(),
        id = purposeId.get(),
    )

    override fun toString(): String {
        return "Purpose(" +
                "description=$description, " +
                "purposeId=$purposeId, " +
                "name=$name, " +
                "category=$category, " +
                "deadline=${Converters.calendarToString("dd.MM.yyyy").convert(deadline.value)}), " +
                "isValid=$isValid"
    }

    companion object {
        fun fromDomainPurpose(purpose: DomainPurpose, category: Category): Purpose {
            return Purpose(
                name = purpose.name,
                category = category,
                deadline = purpose.deadline,
                description = purpose.description,
                purposeId = purpose.id
            )
        }
    }
}
