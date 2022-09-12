package ru.stolexiy.catman.core

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
    Timber.e(exception.stackTraceToString())
}

val defaultCoroutineContext
    get() = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).coroutineContext
val ioCoroutineContext
    get() = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).coroutineContext