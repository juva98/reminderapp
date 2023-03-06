package com.jukvau.reminderapp

import android.content.Context
import androidx.room.Room
import com.jukvau.reminderapp.data.repository.CategoryRepository
import com.jukvau.reminderapp.data.repository.ReminderRepository
import com.jukvau.reminderapp.data.room.reminderappDatabase

object Graph {
    lateinit var database: reminderappDatabase

    lateinit var appContext: Context

    val categoryRepository by lazy {
        CategoryRepository(
            categoryDao = database.categoryDao()
        )
    }

    val reminderRepository by lazy {
        ReminderRepository(
            reminderDao = database.reminderDao()
        )
    }

    fun provide(context: Context) {
        appContext = context
        database = Room.databaseBuilder(context, reminderappDatabase::class.java, "data.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}