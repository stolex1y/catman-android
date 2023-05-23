package ru.stolexiy.catman.domain.usecase.purpose

import ru.stolexiy.catman.domain.repository.purpose.PurposeRepository
import javax.inject.Inject

class PurposeDeletingUseCase @Inject constructor(
    private val repository: PurposeRepository
) {
    suspend fun byId(vararg ids: Long): Result<Unit> {
        if (ids.isEmpty())
            return Result.success(Unit)
        return runCatching {
            repository.delete(*ids)
        }
    }

    suspend fun all(): Result<Unit> {
        return runCatching {
            repository.deleteAll()
        }
    }
}
