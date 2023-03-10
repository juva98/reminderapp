package com.jukvau.reminderapp.util

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jukvau.reminderapp.Graph
import com.jukvau.reminderapp.R
import com.jukvau.reminderapp.data.entity.Reminder
import kotlinx.coroutines.delay
import java.lang.Exception
import java.util.*

class NotificationWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters) {

    override fun doWork(): Result {
//        return try {
//            for (i in 0..10) {
//                Log.i("NotificationWorker", "Counted $i")
//            }
//            Result.success()
//        } catch (e: Exception) {
//            Result.failure()
//        }
        val data = inputData
        val reminderMessage = data.getString("message")
        val reminderCategory = data.getLong("category", 0L)
//        val reminderDelay = data.getLong("delay", 0L)
        showReminderNotification(reminderMessage, reminderCategory)
        return Result.success()
    }

    private fun showReminderNotification(reminder_message: String?, reminder_category: Long) {
        val notificationId = 4
        val success = 1
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Reminder")
//            .setContentText(reminder_message)
            .setContentText(reminder_category.toString())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(Graph.appContext)) {
            notify(notificationId, builder.build())
        }
//        return Result.success()
    }

}