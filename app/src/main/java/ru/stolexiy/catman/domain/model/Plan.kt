package ru.stolexiy.catman.domain.model

import java.util.Date

data class Plan(
    val date: Date,
    val tasks: List<Task>
) {
}