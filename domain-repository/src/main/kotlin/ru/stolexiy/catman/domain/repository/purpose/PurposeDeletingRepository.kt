package ru.stolexiy.catman.domain.repository.purpose

interface PurposeDeletingRepository {
    suspend fun byId(vararg ids: Long): Result<Unit>
    suspend fun all(): Result<Unit>
}
