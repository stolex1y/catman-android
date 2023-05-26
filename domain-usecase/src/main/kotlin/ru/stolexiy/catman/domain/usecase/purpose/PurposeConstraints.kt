package ru.stolexiy.catman.domain.usecase.purpose

import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.usecase.category.CategoryGettingUseCase
import ru.stolexiy.common.DateUtils.isNotPast

internal object PurposeConstraints {
    suspend fun DomainPurpose.checkCategoryIsExist(
        categoryRepository: CategoryGettingUseCase
    ) {
        require(categoryRepository.isExist(categoryId).getOrNull() ?: false) {
            "Parent category must be exist"
        }
    }

    fun DomainPurpose.checkDeadlineIsNotPast() {
        require(deadline.isNotPast()) { "The deadline can't be past" }
    }
}
