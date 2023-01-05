package ru.stolexiy.catman.domain.model

import java.util.Calendar

data class DomainPlan(
    val date: Calendar,
    val tasks: List<DomainTask> = listOf()
) {


}