package ru.stolexiy.catman.data.repository

data class Sort(
    val field: String,
    val direction: Direction = Direction.ASC,
    var then: Sort? = null
) {
    val query: String = ""
        get() {
            val query: StringBuilder = StringBuilder("$field ${direction.value}")
            var curSort: Sort = this
            while (curSort.then != null) {
                curSort = curSort.then!!
                query.append(", ${curSort.field} ${curSort.direction.value}")
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
        return sort
    }

    enum class Direction(val value: String) {
        ASC("ASC"),
        DESC("DESC")
    }
}
