package ru.stolexiy.catman.domain.repository.category

interface CategoryDeletingRepository {
    suspend fun byId(vararg ids: Long): Result<Unit>
    suspend fun all(): Result<Unit>
}
