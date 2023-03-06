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
//        val reminderDelay = data.getLong("delay", 0L)
        showReminderNotification(reminderMessage)
        return Result.success()
    }

    fun showReminderNotification(reminder_message: String?) {
        val notificationId = 4
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Reminder")
            .setContentText(reminder_message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(Graph.appContext)) {
            notify(notificationId, builder.build())
        }
    }

}