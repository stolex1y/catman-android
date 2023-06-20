package ru.stolexiy.common.di

import kotlinx.coroutines.CoroutineDispatcher

fun interface SingleThreadDispatcherProvider {
    fun get(threadName: String): CoroutineDispatcher
}
