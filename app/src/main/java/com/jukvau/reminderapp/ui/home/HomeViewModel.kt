package com.jukvau.reminderapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jukvau.reminderapp.Graph
import com.jukvau.reminderapp.data.entity.Category
import com.jukvau.reminderapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val categoryRepository: CategoryRepository = Graph.categoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    private val _selectedCategory = MutableStateFlow<Category?>(null)

    val state: StateFlow<HomeViewState>
        get() = _state

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }

    init {
        viewModelScope.launch {

            combine(
                categoryRepository.categories().onEach { list ->
                    if (list.isNotEmpty() && _selectedCategory.value == null) {
                        _selectedCategory.value = list[0]
                    }
                },
                _selectedCategory
            ) { categories, selectedCategory ->
                HomeViewState(
                    categories = categories,
                    selectedCategory = selectedCategory
                )
            }.collect { _state.value = it }
        }

        loadCategoriesFromDb()
    }

    private fun loadCategoriesFromDb() {
        val list = mutableListOf(
            Category(name = "Reminder"),
            Category(name = "To-do"),
            Category(name = "Urgent"),
            Category(name = "Seen")
//            Category(name = "Thing 5"),
//            Category(name = "Thing 6"),
//            Category(name = "Thing 7"),
//            Category(name = "Thing 8"),
//            Category(name = "Thing 9"),
//            Category(name = "Thing 10"),
//            Category(name = "Thing 11")
        )
        viewModelScope.launch {
            list.forEach { category -> categoryRepository.addCategory(category) }
        }
    }
}

data class HomeViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)