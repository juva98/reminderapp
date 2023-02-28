package com.jukvau.reminderapp.ui.home.categoryReminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jukvau.reminderapp.Graph
import com.jukvau.reminderapp.data.repository.ReminderRepository
import com.jukvau.reminderapp.data.room.ReminderToCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class CategoryReminderViewModel(
    private val categoryId: Long,
    private val reminderRepository: ReminderRepository = Graph.reminderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryReminderViewState())
    val state: StateFlow<CategoryReminderViewState>
        get() = _state

    init {
        viewModelScope.launch {
            reminderRepository.remindersInCategory(categoryId).collect { list ->
                _state.value = CategoryReminderViewState(
                    reminders = list
                )
            }
        }
    }
}

data class CategoryReminderViewState(
    val reminders: List<ReminderToCategory> = emptyList()
)