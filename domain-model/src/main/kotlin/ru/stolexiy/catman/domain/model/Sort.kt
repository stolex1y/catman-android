package ru.stolexiy.catman.domain.model

class Sort<EntityField : Field> private constructor(
    val field: EntityField,
    val direction: Direction = Direction.ASC
) {
    companion object {
        fun <EntityField : Field> desc(field: EntityField) = Sort(
            field,
            Direction.DESC
        )

        fun <EntityField : Field> asc(field: EntityField) = Sort(
            field,
            Direction.ASC
        )
    }

    fun query(map: EntityField.() -> String): String {
        return "${field.map()} ${direction.value}"
    }

    @Suppress("VisibleForTests")
    override fun toString(): String {
        return "Sort(field=${field.codeName}, direction=${direction.name})"
    }

    enum class Direction(val value: String) {
        ASC("ASC"),
        DESC("DESC")
    }
}
