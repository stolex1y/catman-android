package ru.stolexiy.catman.ui.dialog.task.model

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.ui.dialog.common.model.Category
import java.time.LocalDate

data class Purpose(
    val id: Long,
    val name: String,
    val category: Category,
    val deadline: LocalDate,
)

fun DomainPurpose.toPurpose(category: Category) = Purpose(
    id = id,
    name = name,
    category = category,
    deadline = deadline.toLocalDate(),
)
