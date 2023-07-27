package ru.stolexiy.catman.domain.model

sealed class DomainWorkRegularity(
    val id: Long
) {
    class Period(id: Long, val period: Int) : DomainWorkRegularity(id)
    class Weekdays(id: Long, vararg days: DayOfTheWeek) : DomainWorkRegularity(id) {
        val days = setOf(days)
    }

    enum class Type {
        PERIOD,
        WEEKDAYS
    }
}
