package ru.stolexiy.catman.domain.model

data class Category(
    val name: String,
    val color: Int,
    val id: Long = 0,
    val description: String? = null,
    val purposes: List<Purpose>? = null,
) {
}