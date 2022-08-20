package ru.stolexiy.catman.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val purposes: List<Purpose>?
) {
}