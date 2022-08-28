package ru.stolexiy.catman.core.recyclerview

interface ListItem {
    val id: Long

    fun areContentEquals(other: ListItem): Boolean = this == other
}