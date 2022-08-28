package ru.stolexiy.catman.domain.model

import java.util.Calendar
import java.util.Date

data class Plan(
    val date: Calendar,
    val tasks: List<Task>
) {
}