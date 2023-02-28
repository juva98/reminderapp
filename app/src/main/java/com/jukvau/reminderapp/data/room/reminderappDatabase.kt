package com.jukvau.reminderapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jukvau.reminderapp.data.entity.Category
import com.jukvau.reminderapp.data.entity.Reminder

@Database(
    entities = [Category::class, Reminder::class],
    version = 2,
    exportSchema = false
)
abstract class reminderappDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
}