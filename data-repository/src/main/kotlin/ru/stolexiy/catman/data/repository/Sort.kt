package ru.stolexiy.catman.data.repository

data class Sort(
    val fieldName: String,
    val direction: Direction = Direction.ASC,
) {
    var then: Sort? = null
        private set

    val query: String
        get() {
            val query = StringBuilder("$fieldName ${direction.value}")
            var curSort: Sort = this
            while (curSort.then != null) {
                curSort = curSort.then!!
                query.append(", ${curSort.fieldName} ${curSort.direction.value}")
            }
            return query.toString()
        }

    companion object {
        fun desc(field: String) = Sort(field, Direction.DESC)
        fun asc(field: String) = Sort(field, Direction.ASC)
        fun create(field: String, asc: Boolean) =
            Sort(
                field,
                if (asc) Direction.ASC else Direction.DESC
            )
    }

    fun then(sort: Sort): Sort {
        then = sort
        return this
    }

    enum class Direction(val value: String) {
        ASC("ASC"),
        DESC("DESC")
    }
}
