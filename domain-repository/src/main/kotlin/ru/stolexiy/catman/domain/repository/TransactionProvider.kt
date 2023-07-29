package ru.stolexiy.catman.domain.repository

interface TransactionProvider {
    suspend fun <T> runInTransaction(block: suspend () -> T): T
}
