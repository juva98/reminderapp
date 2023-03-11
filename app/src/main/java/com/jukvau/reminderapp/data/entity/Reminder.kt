package com.jukvau.reminderapp.data.entity

import androidx.room.*

@Entity(
    tableName = "reminders",
    indices = [
        Index("id", unique = true),
        Index("reminder_category_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["reminder_category_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Reminder(
//    val Message: String,
//    val location_x: Long,
//    val location_y: Long,
//    val reminder_time: Long,
//    val creation_time: Long,
//    val creator_id: Long,
//    val reminder_seen: Long,
//    val reminderCategoryId: Long
//    val reminderDate: Date?,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val reminderId: Long = 0,
    @ColumnInfo(name = "reminder_message") val reminderMessage: String,
    @ColumnInfo(name = "reminder_creation_time") val reminderCreationTime: Long,
    @ColumnInfo(name = "reminder_category_id") var reminderCategoryId: Long,
    @ColumnInfo(name = "reminder_creator_id") val reminderCreatorId: Int,
    @ColumnInfo(name = "reminder_x") val reminderX: Double,
    @ColumnInfo(name = "reminder_y") val reminderY: Double,
    @ColumnInfo(name = "reminder_time") val reminderTime: Long,
    @ColumnInfo(name = "reminder_seen") var reminderSeen: Boolean,
)