package ru.stolexiy.catman.domain.model

class PageResponse<EntityField : Field, T>(
    pageRequest: PageRequest<EntityField>,
    data: List<T>,
    count: Long
) {
    val page: Int = pageRequest.page ?: 0

    val size: Int = pageRequest.size ?: Int.MAX_VALUE

    val hasNext: Boolean = (page + 1L) * size.toLong() < count

    val hasPrev: Boolean = page > 0

    val data: List<T>

    init {
        this.data = data.take(size)
    }
}
