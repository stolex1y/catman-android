package ru.stolexiy.catman.domain.model

class PageRequest<EntityField : Field>(
    val page: Int? = null,
    val size: Int? = null,
    val sort: Sort<EntityField>
) {
    val limit: Int
        get() {
            return if (page == null || size == null)
                Int.MAX_VALUE
            else
                size
        }

    val offset: Int
        get() {
            return if (page == null || size == null)
                0
            else
                size * page
        }

    fun getSortQuery(fieldMapper: EntityField.() -> String) = sort.query(fieldMapper)
    override fun toString(): String {
        return "PageRequest(page=$page, size=$size, sort=$sort)"
    }

    companion object {
        fun <EntityField : Field> sortBy(sort: Sort<EntityField>): PageRequest<EntityField> =
            PageRequest(sort = sort)
    }
}
