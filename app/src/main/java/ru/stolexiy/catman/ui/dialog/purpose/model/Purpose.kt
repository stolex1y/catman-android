package ru.stolexiy.catman.ui.dialog.purpose.model

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.ui.util.DefaultConditions
import ru.stolexiy.catman.ui.util.validation.ValidatedEntity
import ru.stolexiy.common.DateUtils
import ru.stolexiy.common.DateUtils.toString
import java.io.Serializable
import java.time.ZonedDateTime

class Purpose(
    name: String = "",
    category: Category? = null,
    deadline: ZonedDateTime? = null,
    var description: String = "",
    val purposeId: Long = 0
) : Serializable, ValidatedEntity() {
    val name: ValidatedProperty<String> = validatedProperty(name, DefaultConditions.notEmpty())
    val category: ValidatedProperty<Category?> = validatedProperty(category, DefaultConditions.notNull())
    val deadline: ValidatedProperty<ZonedDateTime?> = validatedProperty(deadline, DefaultConditions.fromToday())

    fun toDomainPurpose() = DomainPurpose(
        name = name.get(),
        categoryId = category.get()?.id ?: throw IllegalStateException("Is required field"),
        deadline = deadline.get() ?: throw IllegalStateException("Is required field"),
        description = description,
        id = purposeId,
    )

    override fun toString(): String {
        return "Purpose(" +
                "description=$description, " +
                "purposeId=$purposeId, " +
                "name=$name, " +
                "category=$category, " +
                "deadline=${deadline.value?.toString(DateUtils.DMY_DATE)}), " +
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
