package ru.stolexiy.catman.domain.model

import java.io.Serializable
import java.util.Calendar

data class DomainPlan(
    val date: Calendar,
    val tasks: List<DomainTask> = listOf()
) : Serializable
