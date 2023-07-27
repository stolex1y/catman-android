package ru.stolexiy.catman.domain.model

data class DomainCategory(
    val name: String,
    val color: Int,
    val id: Long,
    val description: String,
    val partOfSpentTime: Double,
) {
    constructor(
        name: String,
        color: Int,
        id: Long,
        description: String,
    ) : this(
        name = name,
        color = color,
        id = id,
        description = description,
        partOfSpentTime = 0.0,
    )

    constructor() : this(
        name = "",
        color = 0,
        id = 0,
        description = "",
        partOfSpentTime = 0.0,
    )
}
