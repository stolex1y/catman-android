package ru.stolexiy.catman.data.datasource.local.model

import android.graphics.Color
import kotlinx.coroutines.flow.map
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.model.Purpose
import ru.stolexiy.catman.domain.model.Subtask
import ru.stolexiy.catman.domain.model.Task

fun CategoryEntity.toCategory(purposes: List<Purpose>? = null) = Category(id = id, name = name, color = color, description = description, purposes = purposes)
fun Category.toCategoryEntity() = CategoryEntity(id, name, color, description)

fun Array<out Category>.toCategoryEntities() = map(Category::toCategoryEntity).toTypedArray()
fun Map<CategoryEntity, List<PurposeEntity>>.toCategoriesWithPurposes(): List<Category> = map { entry ->
    entry.key.toCategory(entry.value.map(PurposeEntity::toPurpose))
}

fun Purpose.toPurposeEntity() = PurposeEntity(id = id, name = name, categoryId = categoryId, deadline = deadline, priority = priority, isFinished = isFinished, description = description, progress = progress)
fun PurposeEntity.toPurpose(tasks: List<Task>? = null) = Purpose(id = id, name = name, categoryId = categoryId, deadline = deadline, priority = priority, isFinished = isFinished, description = description, tasks = tasks, progress = progress)
fun Array<out Purpose>.toPurposeEntities() = map(Purpose::toPurposeEntity).toTypedArray()

fun Map<PurposeEntity, List<TaskEntity>>.toPurposeWithTasks(): List<Purpose> = map { entry ->
    entry.key.toPurpose(entry.value.map(TaskEntity::toTask))
}

fun Task.toTaskEntity() = TaskEntity(id = id, name = name, purposeId = purposeId, deadline = deadline, description = description, priority = priority, isFinished = isFinished, progress = progress)
fun TaskEntity.toTask(subtasks: List<Subtask>? = null) = Task(id = id, name = name, purposeId = purposeId, deadline = deadline, description = description, priority = priority, isFinished = isFinished, subtasks = subtasks)
