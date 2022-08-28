package ru.stolexiy.catman.core.recyclerview

class SingleLayoutAdapter<T : ListItem>(private val layoutId: Int) : BaseListAdapter<T>() {
    override fun getLayoutIdForPosition(position: Int): Int {
        return layoutId
    }
}