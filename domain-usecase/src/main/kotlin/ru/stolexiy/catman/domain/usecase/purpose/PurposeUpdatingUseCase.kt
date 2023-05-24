package ru.stolexiy.catman.domain.usecase.purpose

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.purpose.PurposeRepository
import javax.inject.Inject

class PurposeUpdatingUseCase @Inject constructor(
    private val purposeRepository: PurposeRepository,
) {
    suspend operator fun invoke(vararg purposes: DomainPurpose): Result<Unit> {
        if (purposes.isEmpty())
            return Result.success(Unit)
        return runCatching {
            purposeRepository.update(*purposes)
        }
    }
}
