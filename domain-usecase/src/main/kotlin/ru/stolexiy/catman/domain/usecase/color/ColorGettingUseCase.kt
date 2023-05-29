package ru.stolexiy.catman.domain.usecase.color

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.DomainColor
import ru.stolexiy.catman.domain.repository.ColorRepository
import ru.stolexiy.common.FlowExtensions.mapToResult
import javax.inject.Inject

class ColorGettingUseCase @Inject constructor(
    private val repository: ColorRepository
) {
    fun all(): Flow<Result<List<DomainColor>>> =
        repository.getAll().mapToResult()

    fun byId(color: Int): Flow<Result<DomainColor?>> =
        repository.get(color).mapToResult()
}
