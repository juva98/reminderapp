package com.jukvau.reminderapp.data.repository

import com.jukvau.reminderapp.data.entity.Category
import com.jukvau.reminderapp.data.room.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun categories(): Flow<List<Category>> = categoryDao.categories()
    fun getCategoryWithId(categoryId: Long): Category? = categoryDao.getCategoryWithId(categoryId)

    suspend fun addCategory(category: Category): Long {
        return when (val local = categoryDao.getCategoryWithName(category.name)) {
            null -> categoryDao.insert(category)
            else -> local.id
        }
    }
}