package ru.stolexiy.catman.data.mapper

import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.model.Purpose
import ru.stolexiy.catman.domain.model.Task

fun CategoryEntity.toCategory(purposes: List<Purpose>? = null) = Category(id, name, purposes)
fun Category.toCategoryEntity() = CategoryEntity(id, name)

//TODO isFinished
fun Purpose.toPurposeEntity() = PurposeEntity(id, name, categoryId, deadline, priority, false)
fun PurposeEntity.toPurpose(tasks: List<Task>? = null) = Purpose(id, name, categoryId, deadline, priority, tasks)
