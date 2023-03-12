package com.jukvau.reminderapp.ui.reminder

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.*
import com.google.android.gms.maps.model.LatLng
import com.jukvau.reminderapp.Graph
import com.jukvau.reminderapp.data.entity.Category
import com.jukvau.reminderapp.data.entity.Reminder
import com.jukvau.reminderapp.data.repository.ReminderRepository
import com.jukvau.reminderapp.data.room.ReminderToCategory
import com.jukvau.reminderapp.data.room.reminderappDatabase
import kotlinx.coroutines.launch
import java.util.*
import com.jukvau.reminderapp.datastore.StoreUserData
import com.jukvau.reminderapp.util.NotificationWorker
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit
import android.location.Location

public var remindersList = mutableListOf<ReminderToCategory>()

@Composable
fun Reminder(
    onBackPress: () -> Unit,
    viewModel: ReminderViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreUserData(context)
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val message = rememberSaveable { mutableStateOf("No title") }
    val category = rememberSaveable { mutableStateOf("") }
    val timeH = rememberSaveable { mutableStateOf("0") }
    val timeM = rememberSaveable { mutableStateOf("0") }
    val timeS = rememberSaveable { mutableStateOf("0") }
    val reminderTimed = remember{mutableStateOf(false)}
    var latitude = 0.00
    var longitude = 0.00

    val latlng = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<LatLng>("location_data")
        ?.value

//    var remindercategory = 0L
//    var remindertime = 0L


    getAllReminders()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar {
                IconButton(
                    onClick = onBackPress
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(text = "Reminder")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)
            ) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = message.value,
                    onValueChange = { message.value = it },
                    label = { Text(text = "Reminder message") },
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))
                if (latlng == null) {
                    OutlinedButton(
                        onClick = { navController.navigate("map") },
                        modifier = Modifier.height(55.dp)
                    ) {
                        Text(text = "Reminder location")
                    }
                } else {
                    latitude = latlng.latitude
                    longitude = latlng.longitude
                    Text(
                        text = "Lat: ${latlng.latitude}, Lng: ${latlng.longitude}"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                CategoryListDropdown(
                    viewState = viewState,
                    category = category
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = timeH.value,
                        onValueChange = { timeH.value = it },
                        label = { Text(text = "Hours") },
                        shape = RoundedCornerShape(corner = CornerSize(50.dp)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = timeM.value,
                        onValueChange = { timeM.value = it },
                        label = { Text(text = "Minutes") },
                        shape = RoundedCornerShape(corner = CornerSize(50.dp)),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = timeS.value,
                        onValueChange = { timeS.value = it },
                        label = { Text(text = "Seconds") },
                        shape = RoundedCornerShape(corner = CornerSize(50.dp)),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                }

                Spacer(modifier = Modifier.height(10.dp))

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {
                        coroutineScope.launch {
                            if (latitude != 0.00) {
                                viewModel.saveReminder(
                                    com.jukvau.reminderapp.data.entity.Reminder(
                                        reminderMessage = message.value,
                                        reminderX = latitude,
                                        reminderY = longitude,
                                        reminderCreationTime = Date().time,
                                        reminderCategoryId = getCategoryId(
                                            viewState.categories,
                                            category.value
                                        ),
                                        reminderCreatorId = StoreUserData.USER_ID,
                                        reminderTime = Date(
                                            Date().time
                                                    + timeH.value.toInt() * 60 * 60 * 1000
                                                    + timeM.value.toInt() * 60 * 1000
                                                    + timeS.value.toInt() * 1000
                                        ).time,
                                        reminderSeen = false
                                    )
                                )
                                viewModel.createReminderMadeNotification(message.value, (Date().time
                                        + timeH.value.toLong() * 3600000
                                        + timeM.value.toLong() * 60000
                                        + timeS.value.toLong() * 1000))
                            } else {
                                if (latitude == 0.00) {
                                    viewModel.saveReminder(
                                        com.jukvau.reminderapp.data.entity.Reminder(
                                            reminderMessage = message.value,
                                            reminderX = 0.00,
                                            reminderY = 0.00,
                                            reminderCreationTime = Date().time,
                                            reminderCategoryId = getCategoryId(
                                                viewState.categories,
                                                category.value
                                            ),
                                            reminderCreatorId = StoreUserData.USER_ID,
                                            reminderTime = Date(
                                                Date().time
                                                        + timeH.value.toInt() * 60 * 60 * 1000
                                                        + timeM.value.toInt() * 60 * 1000
                                                        + timeS.value.toInt() * 1000
                                            ).time,
                                            reminderSeen = false
                                        )
                                    )
                                    viewModel.createReminderMadeNotification(message.value, (Date().time
                                            + timeH.value.toLong() * 3600000
                                            + timeM.value.toLong() * 60000
                                            + timeS.value.toLong() * 1000))
                                }
                            }
                        }
                        onBackPress()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                ) {
                    Text(text = "Save reminder")
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {
                        val list = remindersList
                        var counter = 1
                        for (rem in list) {
                            var remlocX = rem.reminder.reminderX
                            var remlocY = rem.reminder.reminderY
                            Log.i("fndrem", "found reminder $counter")
                            Log.i("remlatlng", "$remlocX $remlocY")
                            counter++

                            var distArray = FloatArray(3)
                            Location.distanceBetween(latitude, longitude, remlocX, remlocY, distArray)
                            Log.i("remdist", "${distArray[0]}")
                            if (distArray[0] <= 1000.000) {
                                Log.i("distgood", "reminder nearby")
                                if (rem.reminder.reminderCategoryId == 2L) {
                                    coroutineScope.launch {
                                        viewModel.removeReminder(
                                            com.jukvau.reminderapp.data.entity.Reminder(
                                                reminderId = rem.reminder.reminderId,
                                                reminderMessage = rem.reminder.reminderMessage,
                                                reminderX = remlocX,
                                                reminderY = remlocY,
                                                reminderCreationTime = rem.reminder.reminderCreationTime,
                                                reminderCategoryId = 1.toLong(),
                                                reminderCreatorId = rem.reminder.reminderCreatorId,
                                                reminderTime = rem.reminder.reminderTime,
                                                reminderSeen = false
                                            )
                                        )
                                    }

                                    coroutineScope.launch {
                                        viewModel.saveReminder(
                                            com.jukvau.reminderapp.data.entity.Reminder(
                                                reminderId = rem.reminder.reminderId,
                                                reminderMessage = rem.reminder.reminderMessage,
                                                reminderX = 0.00,
                                                reminderY = 0.00,
                                                reminderCreationTime = rem.reminder.reminderCreationTime,
                                                reminderCategoryId = 2.toLong(),
                                                reminderCreatorId = rem.reminder.reminderCreatorId,
                                                reminderTime = rem.reminder.reminderTime,
                                                reminderSeen = true
                                            )
                                        )
                                    }
                                }
                                viewModel.createSuccessNotification(rem.reminder)
                            }

                    }
                        onBackPress()
                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                ) {
                    Text(text = "Check Location")
                }

            }
        }
    }
}

private fun getCategoryId(categories: List<Category>, categoryName: String): Long {
    return categories.first { category -> category.name == categoryName }.id
}

@Composable
private fun CategoryListDropdown(
    viewState: ReminderViewState,
    category: MutableState<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) {
        Icons.Filled.ArrowDropUp
    } else {
        Icons.Filled.ArrowDropDown
    }

    Column {
        OutlinedTextField(
            value = category.value,
            onValueChange = { category.value = it},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Category") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            viewState.categories.forEach { dropDownOption ->
                DropdownMenuItem(
                    onClick = {
                        category.value = dropDownOption.name
                        expanded = false
                    }
                ) {
                    Text(text = dropDownOption.name)
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun getAllReminders(viewModel: ReminderViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()

    remindersList.clear()

    val reminders = mutableListOf<ReminderToCategory>()
    coroutineScope.launch {
        viewModel.getReminder(2.toLong()).collect {reminderToCategoryList ->
            reminders.addAll(reminderToCategoryList)
        }
    }
    coroutineScope.launch {
        viewModel.getReminder(1.toLong()).collect {reminderToCategoryList ->
            reminders.addAll(reminderToCategoryList)
        }
    }

    remindersList = reminders

}

fun createNotificationChannel2(context: Context) {
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

fun timedReminder(delay: Long) {
    val workManager = WorkManager.getInstance(Graph.appContext)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setConstraints(constraints)
        .build()

    workManager.enqueue(notificationWorker)

//Monitoring for state of work (jos ei toimi, kommentoi pois tms)
    workManager.getWorkInfoByIdLiveData(notificationWorker.id)
        .observeForever { workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED) {

            }
        }
}