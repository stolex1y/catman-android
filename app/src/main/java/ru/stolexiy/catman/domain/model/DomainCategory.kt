package ru.stolexiy.catman.domain.model

import java.io.Serializable

data class DomainCategory(
    val name: String,
    val color: Int,
    val id: Long = 0,
    val description: String? = null
) : Serializable
