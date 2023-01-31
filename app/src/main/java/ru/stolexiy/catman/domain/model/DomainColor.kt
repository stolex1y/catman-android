package ru.stolexiy.catman.domain.model

import java.io.Serializable

data class DomainColor(
    val color: Int,
    val name: String
) : Serializable
