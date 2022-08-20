package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.Entity
import ru.stolexiy.catman.core.DayOfTheWeek


@Entity(
    tableName = "work_by_weekdays",
    primaryKeys = ["id", "weekday"]
)
data class RegularWorkEntity(
    val id: Long,
    val weekday: DayOfTheWeek?,
    val period: Int?
) {
    init {
        require(weekday != null || period != null)
    }
}