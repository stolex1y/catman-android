package ru.stolexiy.catman.ui.util.recyclerview

interface ListItem {
    val id: Long

    fun areContentEquals(other: ListItem): Boolean = this == other
}