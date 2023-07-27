package ru.stolexiy.catman.domain.repository.task

interface TaskDeletingRepository {
    suspend fun byId(vararg ids: Long): Result<Unit>
    suspend fun all(): Result<Unit>
}
