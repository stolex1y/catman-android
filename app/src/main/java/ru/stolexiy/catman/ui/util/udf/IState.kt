package ru.stolexiy.catman.ui.util.udf

import androidx.annotation.StringRes

interface IState {
    interface Producer<S : IState> {
        val initState: S
        val loadedState: S
        fun errorState(@StringRes error: Int): S
    }
}
