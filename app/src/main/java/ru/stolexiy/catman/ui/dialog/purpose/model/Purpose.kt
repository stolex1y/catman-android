package ru.stolexiy.catman.ui.dialog.purpose.model

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.ui.dialog.common.model.Category
import ru.stolexiy.catman.ui.util.DefaultConditions
import ru.stolexiy.catman.ui.util.validation.ValidatedEntity
import ru.stolexiy.common.DateUtils
import ru.stolexiy.common.DateUtils.getDayLastMoment
import ru.stolexiy.common.DateUtils.toString
import java.time.LocalDate

class Purpose(
    name: String,
    category: Category?,
    deadline: LocalDate?,
    var description: String,
    val purposeId: Long
) : ValidatedEntity() {
    val name: ValidatedProperty<String> = validatedProperty(name, DefaultConditions.notEmpty())
    val category: ValidatedProperty<Category?> =
        validatedProperty(category, DefaultConditions.notNull())
    val deadline: ValidatedProperty<LocalDate?> =
        validatedProperty(deadline, DefaultConditions.dateFromToday() * DefaultConditions.notNull())

    constructor() : this(
        name = "",
        category = null,
        deadline = null,
        description = "",
        purposeId = 0
    )

    fun toDomainPurpose() = DomainPurpose(
        name = name.get(),
        categoryId = category.get()!!.id,
        deadline = deadline.get()!!.getDayLastMoment(),
        description = description,
        id = purposeId,
        isFinished = false,
        priority = 0,
        progress = 0.0,
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
                deadline = purpose.deadline.toLocalDate(),
                description = purpose.description,
                purposeId = purpose.id
            )
        }
    }
}
