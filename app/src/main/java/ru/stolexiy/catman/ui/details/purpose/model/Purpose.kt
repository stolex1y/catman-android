package ru.stolexiy.catman.ui.details.purpose.model

import ru.stolexiy.catman.domain.model.DomainPurpose
import java.time.LocalDate

data class Purpose(
    val id: Long,
    val name: String,
    val category: Category,
    val deadline: LocalDate
) {
    constructor() : this(
        id = 0,
        name = "",
        category = Category(),
        deadline = LocalDate.now(),
    )
}

fun DomainPurpose.toPurpose(category: Category) = Purpose(
    id = id,
    name = name,
    category = category,
    deadline = deadline.toLocalDate(),
)
