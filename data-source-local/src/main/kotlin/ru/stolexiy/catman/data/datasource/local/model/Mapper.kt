package ru.stolexiy.catman.data.datasource.local.model;

import ru.stolexiy.catman.domain.model.DomainCategory
import ru.stolexiy.catman.domain.model.DomainPurpose

fun Map<CategoryEntity, List<PurposeEntity>>.toDomainMap(): Map<DomainCategory, List<DomainPurpose>> {
    val domainMap = mutableMapOf<DomainCategory, List<DomainPurpose>>()
    this.forEach { mapEntry ->
        val categoryEntity = mapEntry.key
        val purposeEntityList = mapEntry.value
        domainMap[categoryEntity.toDomainCategory()] = purposeEntityList.map { it.toDomainPurpose() }
    }
    return domainMap
}
