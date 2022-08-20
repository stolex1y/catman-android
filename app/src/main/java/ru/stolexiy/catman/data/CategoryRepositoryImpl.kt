package ru.stolexiy.catman.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.stolexiy.catman.data.datasource.local.dao.LocalCategoryDao
import ru.stolexiy.catman.data.mapper.toCategory
import ru.stolexiy.catman.data.mapper.toCategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.CategoryEntity
import ru.stolexiy.catman.data.datasource.local.model.PurposeEntity
import ru.stolexiy.catman.data.mapper.toPurpose
import ru.stolexiy.catman.domain.model.Category
import ru.stolexiy.catman.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val localDao: LocalCategoryDao
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> =
        localDao.getAll().map { it.map(CategoryEntity::toCategory) }

    override fun getCategory(id: Long): Flow<Category> =
        localDao.get(id).map(CategoryEntity::toCategory)

    override suspend fun updateCategory(category: Category): Flow<Category> {
        val id = category.id
        localDao.update(category.toCategoryEntity())
        return getCategory(id)
    }

    override suspend fun deleteCategory(category: Category) {
        localDao.delete(category.toCategoryEntity())
    }

    override suspend fun insertCategory(category: Category): Flow<Category> {
        val id = localDao.insert(category.toCategoryEntity())
        return getCategory(id)
    }

   /* override fun getAllPurposes(categoryId: Long): Flow<List<Purpose>> = localDao.getAllPurposes(categoryId).map {
        it.map(PurposeEntity::toPurpose)
    }*/
   /* override fun getCategoryWithPurposes(id: Long): Flow<Category> {
    }*/

    override fun getAllCategoriesWithPurposes(): Flow<List<Category>> = localDao.getAllWithPurposes().map {
        it.map { entry ->
            entry.key.toCategory(entry.value.map(PurposeEntity::toPurpose))
        }
    }
}