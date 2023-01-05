package ru.stolexiy.catman.ui.dialog.purpose.model

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.ui.mapper.toDmyString
import java.io.Serializable
import java.util.*

data class Purpose(
    var name: String = "",
    var categoryId: Long? = null,
    var deadline: Calendar? = null,
    var description: String = ""
) : Serializable {
    val textDeadline: String
        get() = deadline?.toDmyString() ?: ""

    fun toDomainPurpose() = DomainPurpose(
        name = name,
        categoryId = categoryId ?: throw IllegalStateException("Is required field"),
        deadline = deadline ?: throw IllegalStateException("Is required field"),
        description = if (description == "") null else description
    )
}
