package ru.stolexiy.catman.ui.util.state

import kotlinx.coroutines.flow.StateFlow

interface ViewStateOwner<T : ViewState> {
    val state: StateFlow<T>
}