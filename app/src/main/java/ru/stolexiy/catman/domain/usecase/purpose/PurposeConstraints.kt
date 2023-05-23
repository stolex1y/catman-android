package ru.stolexiy.catman.domain.usecase.purpose

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.category.CategoryGettingUseCase

object PurposeConstraints {
    internal suspend fun requireExistCategory(
        purpose: DomainPurpose,
        categoryRepository: CategoryGettingUseCase
    ) {
        require(categoryRepository.isExist(purpose.categoryId).getOrNull() ?: false) {
            "Parent category must be exist"
        }
    }
}
