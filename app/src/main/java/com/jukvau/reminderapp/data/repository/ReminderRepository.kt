package com.jukvau.reminderapp.data.repository

import com.jukvau.reminderapp.data.entity.Reminder
import com.jukvau.reminderapp.data.room.ReminderDao
import com.jukvau.reminderapp.data.room.ReminderToCategory
import kotlinx.coroutines.flow.Flow

/**
 * data repository for [Reminder] instences
 */
class ReminderRepository(
    private val reminderDao: ReminderDao,
) {
    /**
     * returns flow w/ list of reminders associated with category within given [categoryId]
     */
    fun remindersInCategory(categoryId: Long) : Flow<List<ReminderToCategory>> {
        return reminderDao.remindersFromCategory(categoryId)
    }
    /**
     * add new [Reminder] to reminder store
     */
    suspend fun addReminder(reminder: Reminder) = reminderDao.insert(reminder)

    suspend fun editReminder(reminder: Reminder) = reminderDao.update(reminder)

    suspend fun removeReminder(reminder: Reminder) = reminderDao.delete(reminder)

    suspend fun getReminder(reminderid: Long) = reminderDao.reminder(reminderid)
}