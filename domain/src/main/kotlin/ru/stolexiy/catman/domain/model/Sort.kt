package ru.stolexiy.catman.domain.model

import java.io.Serializable
import java.util.Collections

typealias SortSelector<T, R> = (T) -> R?

class Sort<T> private constructor() : Serializable {
    lateinit var comparator: Comparator<T>
    companion object {
        fun <T> from(selector: SortSelector<T, Comparable<Any>?>, order: Order = Order.ASC): Sort<T> {
            return Sort<T>().apply {
                comparator = compareBy(selector).apply {
                    if (order == Order.DESC)
                        Collections.reverseOrder(this)
                }
            }
        }
    }

    fun then(selector: SortSelector<T, Comparable<Any>?>, order: Order = Order.ASC): Sort<T> {
        comparator = if (order == Order.DESC)
            comparator.then(Collections.reverseOrder(compareBy(selector)))
        else
            comparator.then(compareBy(selector))
        return this
    }

    fun sort(data: List<T>): List<T> {
        return data.sortedWith(comparator)
    }

    enum class Order {
        ASC,
        DESC
    }
}

