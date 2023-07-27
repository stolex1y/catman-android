package ru.stolexiy.catman.domain.usecase.purpose

import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose
import ru.stolexiy.catman.domain.repository.category.CategoryGettingRepository
import ru.stolexiy.common.DateUtils.isNotPast

internal object PurposeConstraints {
    suspend fun DomainPurpose.checkCategoryIsExist(
        categoryGet: CategoryGettingRepository
    ): DomainCategory {
        return categoryGet.byIdOnce(categoryId).getOrThrow()
            ?: throw IllegalArgumentException("Parent category must be exist")
    }

    fun DomainPurpose.checkDeadlineIsNotPast() {
        require(deadline.isNotPast()) { "The deadline can't be past" }
    }
}
