package ru.stolexiy.catman.domain.model

import java.io.Serializable
import java.util.Calendar

data class DomainPomodoro(
    val dateStart: Calendar,
    val taskId: Long,
    val duration: Long
) : Serializable
