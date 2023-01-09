package ru.stolexiy.catman.domain.model

class PageResponse<T> (val pageRequest: PageRequest<T>, _data: List<T>, val hasNext: Boolean) {
    val page: Int
        get() = pageRequest.page

    val size: Int
        get() = pageRequest.size

    val data: List<T>

    init {
        data = _data.take(pageRequest.size)
    }
}
