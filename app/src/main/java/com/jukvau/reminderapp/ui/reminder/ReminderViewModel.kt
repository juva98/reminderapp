package com.jukvau.reminderapp.ui.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.WorkSource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jukvau.reminderapp.Graph
import com.jukvau.reminderapp.R
import com.jukvau.reminderapp.data.entity.Category
import com.jukvau.reminderapp.data.entity.Reminder
import com.jukvau.reminderapp.data.repository.CategoryRepository
import com.jukvau.reminderapp.data.repository.ReminderRepository
import com.jukvau.reminderapp.util.NotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import androidx.core.app.NotificationManagerCompat.from
import androidx.work.*
import com.jukvau.reminderapp.ui.home.categoryReminder.toDateString
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.util.*

class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository,
): ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    suspend fun saveReminder(reminder: Reminder): Long {
        createReminderMadeNotification(reminder)
        if (reminder.reminderCategoryId == 2L) {
            showTimedReminderNotification(reminder)

        }
        return reminderRepository.addReminder(reminder)
    }

    suspend fun editReminder(reminder: Reminder): Unit {
        return reminderRepository.editReminder(reminder)
    }

    init {
        createNotificationChannel(context = Graph.appContext)
//        setOneTimeNotification()
        viewModelScope.launch {
            categoryRepository.categories().collect { categories ->
                _state.value = ReminderViewState(categories)
            }
        }
    }
    private suspend fun showTimedReminderNotification(reminder: Reminder) = coroutineScope {
//        val reminderRepository: ReminderRepository = Graph.reminderRepository
        val workManager = WorkManager.getInstance(Graph.appContext)
        val currentTime = System.currentTimeMillis()
        val diff = reminder.reminderTime - currentTime
        val data = Data.Builder()
            .putString("message", reminder.reminderMessage)
            .putLong("category", reminder.reminderCategoryId)
//        .putLong("delay", diff)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val reminderWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(diff, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
//    var successcheck = 0

        workManager.enqueue(reminderWorkRequest)

        workManager.getWorkInfoByIdLiveData(reminderWorkRequest.id)
            .observeForever { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    reminder.reminderCategoryId = 1.toLong()
                    createSuccessNotification(reminder)

                    viewModelScope.launch {
//                        reminder.reminderCategoryId = 1
                        createErrorNotification()
                        editReminder(reminder)
//                        changeReminderCategory(reminder)
//                        createNotificationChannel(context = Graph.appContext)
//                        categoryRepository.categories().collect { categories ->
//                            _state.value = ReminderViewState(categories)
//                        }
////                        return@launch reminderRepository.editReminder(reminder)
                    }
                }
            }

    }

    suspend fun changeReminderCategory(reminder: Reminder) {
//        val reminderRepository: ReminderRepository = Graph.reminderRepository
//        reminder.reminderCategoryId = 1
        editReminder(reminder)
        createSuccessNotification(reminder)



    }
}

//private fun setOneTimeNotification() {
//    val workManager = WorkManager.getInstance(Graph.appContext)
//    val constraints = Constraints.Builder()
//        .setRequiredNetworkType(NetworkType.CONNECTED)
//        .build()
//
//    val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
//        .setInitialDelay(10, TimeUnit.SECONDS)
//        .setConstraints(constraints)
//        .build()
//
//    workManager.enqueue(notificationWorker)

    //Monitoring for state of work (jos ei toimi, kommentoi pois tms)
//    workManager.getWorkInfoByIdLiveData(notificationWorker.id)
//        .observeForever { workInfo ->
//            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
//                createSuccessNotification()
//            }
//            else {
//                createErrorNotification()
//            }
//        }
//}



private fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "NotificationChannelName"
        val descriptionText = "NotificationChannelDescriptionText"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun createSuccessNotification(reminder: Reminder) {
    val notificationId = 1
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Success! Download Complete")
        .setContentText(reminder.reminderCategoryId.toString())
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }
}

private fun createErrorNotification() {
    val notificationId = 2
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Not successful :(")
        .setContentText("Your countdown did not complete successfully")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }
}

private fun createReminderMadeNotification(reminder: Reminder) {
    val notificationId = 3
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("New reminder made")
        .setContentText("${reminder.reminderMessage} at ${reminder.reminderTime.toDateString()}")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    with(NotificationManagerCompat.from(Graph.appContext)) {
        notify(notificationId, builder.build())
    }
}

private fun showReminderNotification(reminder: Reminder) {

    val notificationId = 5
//    val diff = reminder.reminderTime - Date().time
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Testing")
        .setContentText(reminder.reminderCategoryId.toString())
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    with(NotificationManagerCompat.from(Graph.appContext)) {
        notify(notificationId, builder.build())
    }
}
data class ReminderViewState(
    val categories: List<Category> = emptyList()
)