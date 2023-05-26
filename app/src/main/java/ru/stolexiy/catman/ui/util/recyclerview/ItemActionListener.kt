package ru.stolexiy.catman.ui.util.recyclerview

interface ItemActionListener<T : ListItem> {
    // TODO fun with unchecked cast to call fun with typed argument
    /* this need to transfer BaseListAdapter to their module
    internal fun internalSwipeToEnd(listItem: ListItem) {
        onSwipeToEnd(listItem as T)
    }*/
    fun onClick(item: T) {}
    fun onSwipeToEnd(item: T) {}
    fun onSwipeToStart(item: T) {}
    fun onMoveTo(item: T, to: T) {}
}
