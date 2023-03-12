package ru.stolexiy.catman.ui.util.recyclerview

class SingleLayoutAdapter<T : ListItem>(private val layoutId: Int) : BaseListAdapter<T>() {
    override fun getLayoutIdForPosition(position: Int): Int {
        return layoutId
    }
}