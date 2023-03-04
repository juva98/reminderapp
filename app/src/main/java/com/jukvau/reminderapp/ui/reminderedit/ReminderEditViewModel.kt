package com.jukvau.reminderapp.ui.reminderedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jukvau.reminderapp.Graph
import com.jukvau.reminderapp.data.entity.Category
import com.jukvau.reminderapp.data.entity.Reminder
import com.jukvau.reminderapp.data.repository.CategoryRepository
import com.jukvau.reminderapp.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReminderEditViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository,
): ViewModel() {
    private val _state = MutableStateFlow(ReminderEditViewState())
    private val reminderid: Long = 0

    val state: StateFlow<ReminderEditViewState>
        get() = _state

    suspend fun saveReminder(reminder: Reminder): Long {
        return reminderRepository.addReminder(reminder)
    }

    suspend fun editReminder(reminder: Reminder): Unit {
        return reminderRepository.editReminder(reminder)
    }

    suspend fun removeReminder(reminder: Reminder): Int {
        return reminderRepository.removeReminder(reminder)
    }

//    fun getReminder(reminderid: Long) {
//        viewModelScope.launch {
//            reminderRepository.getReminder(reminderid)
//        }
//    }
    suspend fun getReminder(reminderid: Long): Reminder? {
        return reminderRepository.getReminder(reminderid)
    }

    init {
        viewModelScope.launch {
            categoryRepository.categories().collect { categories ->
                _state.value = ReminderEditViewState(categories)
            }
        }
    }
}

data class ReminderEditViewState(
    val categories: List<Category> = emptyList()
)