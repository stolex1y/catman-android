package ru.stolexiy.catman.domain.model

import java.io.Serializable

/**
 * @param color in argb format
 */
data class DomainColor(
    val color: Int,
    val name: Int
) : Serializable
