package com.jukvau.reminderapp.ui.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import androidx.work.*
import com.jukvau.reminderapp.data.room.ReminderToCategory
import com.jukvau.reminderapp.ui.home.categoryReminder.toDateString
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository,
): ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    suspend fun saveReminder(reminder: Reminder): Long {
//        createReminderMadeNotification(reminder)
        if (reminder.reminderCategoryId == 2L) {
            showTimedReminderNotification(reminder)

        }
        return reminderRepository.addReminder(reminder)
    }

    private suspend fun editReminder(reminder: Reminder): Unit {
        createErrorNotification()
        reminderRepository.editReminder(reminder)
        categoryRepository.categories().collect { categories ->
            _state.value = ReminderViewState(categories)
        }
    }

    suspend fun removeReminder(reminder: Reminder): Int {
        return reminderRepository.removeReminder(reminder)
    }
    suspend fun getReminder(categoryID: Long): Flow<List<ReminderToCategory>> {
        return reminderRepository.remindersInCategory(categoryID)
    }

    init {
        createNotificationChannel(context = Graph.appContext)
        viewModelScope.launch {
            categoryRepository.categories().collect { categories ->
                _state.value = ReminderViewState(categories)
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun showTimedReminderNotification(reminder: Reminder) = coroutineScope {
        val workManager = WorkManager.getInstance(Graph.appContext)
        val currentTime = System.currentTimeMillis()
        val diff = reminder.reminderTime - currentTime
        val data = Data.Builder()
            .putString("message", reminder.reminderMessage)
            .putLong("category", reminder.reminderCategoryId)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val reminderWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(diff, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(reminderWorkRequest)

        workManager.getWorkInfoByIdLiveData(reminderWorkRequest.id)
            .observeForever { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    reminder.reminderCategoryId = 1.toLong()
//                    createSuccessNotification(reminder)


                    viewModelScope.launch(
                        start = CoroutineStart.ATOMIC
                    ) {
//                        reminder.reminderCategoryId = 1.toLong()
//                        createErrorNotification()
                        editReminder(reminder)
//                        categoryRepository.categories().collect { categories ->
//                            _state.value = ReminderViewState(categories)
//                        }
                    }
                    viewModelScope.launch(
                        start = CoroutineStart.ATOMIC
                    ) {
                        categoryRepository.categories().collect { categories ->
                            _state.value = ReminderViewState(categories)
                        }
                    }
                }
            }

    }

    fun createErrorNotification() {
        val notificationId = 2
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("checkpoint")
            .setContentText("coroutinescope works")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(Graph.appContext)) {
            //notificationId is unique for each notification that you define
            notify(notificationId, builder.build())
        }
    }
    fun createSuccessNotification(reminder: Reminder) {
        val notificationId = Random.nextInt(501, 1001)
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Reminder nearby!")
            .setContentText(reminder.reminderMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(Graph.appContext)) {
            //notificationId is unique for each notification that you define
            notify(notificationId, builder.build())
        }
    }
    fun createReminderMadeNotification(reminderMSG: String, reminderTM: Long) {
        val notificationId = Random.nextInt(50, 500)
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("New reminder made")
            .setContentText("${reminderMSG} at ${reminderTM.toDateString()}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(Graph.appContext)) {
            notify(notificationId, builder.build())
        }
    }
}



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

data class ReminderViewState(
    val categories: List<Category> = emptyList()
)