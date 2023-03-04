package com.jukvau.reminderapp.ui.reminder

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jukvau.reminderapp.data.entity.Category
import com.jukvau.reminderapp.data.entity.Reminder
import com.jukvau.reminderapp.data.room.ReminderDao
import kotlinx.coroutines.launch
import java.util.*
import com.jukvau.reminderapp.datastore.StoreUserData
import com.jukvau.reminderapp.ui.reminderedit.ReminderEditViewModel
import com.jukvau.reminderapp.ui.reminderedit.ReminderEditViewState

@Composable
fun ReminderEdit(
//    onBackPress: NavController,
    onBackPress: () -> Unit,
    reminderId: String?,
    navController: NavController,
    viewModel: ReminderEditViewModel = viewModel(),
//    reminder: Reminder
//    reminderDao: ReminderDao
) {
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val message = rememberSaveable { mutableStateOf("") }
    val category = rememberSaveable { mutableStateOf("") }
    val timeH = rememberSaveable { mutableStateOf("") }
    val timeM = rememberSaveable { mutableStateOf("") }
    val timeS = rememberSaveable { mutableStateOf("") }
//    val oldreminder = reminderId?.let { viewModel.getReminder(it.toLong()) }

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
//                Spacer(modifier = Modifier.height(10.dp))
//                OutlinedTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = time.value,
//                    onValueChange = { time.value = it },
//                    label = { Text(text = "Hours to reminder") },
//                    shape = RoundedCornerShape(corner = CornerSize(50.dp)),
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Number
//                    )
//                )
                Spacer(modifier = Modifier.height(10.dp))
                CategoryListDropdown2(
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
                            if (reminderId != null) {
                                viewModel.editReminder(
                                    com.jukvau.reminderapp.data.entity.Reminder(
                                        reminderId = reminderId.toLongOrNull()!!,
                                        reminderMessage = message.value,
                                        reminderX = 0,
                                        reminderY = 0,
                                        reminderCreationTime = Date().time,
                                        reminderCategoryId = getCategoryId(viewState.categories, category.value),
                                        reminderCreatorId = StoreUserData.USER_ID,
                                        reminderTime = Date(Date().time
                                                + timeH.value.toInt() * 60 * 60 * 1000
                                                + timeM.value.toInt() * 60 * 1000
                                                + timeS.value.toInt() * 1000).time,
                                        reminderSeen = false
                                    )
                                )
                            }
                        }
                        onBackPress()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                ) {
                    Text(text = "Edit reminder")
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {
                        coroutineScope.launch {
                            if (reminderId != null) {
                                viewModel.removeReminder(
                                    com.jukvau.reminderapp.data.entity.Reminder(
                                        reminderId = reminderId.toLongOrNull()!!,
                                        reminderMessage = "",
                                        reminderX = 0,
                                        reminderY = 0,
                                        reminderCreationTime = 0,
                                        reminderCategoryId = 0,
                                        reminderCreatorId = 0,
                                        reminderTime = 0,
                                        reminderSeen = false
                                    )
                                )
                            }
                        }
                        onBackPress()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                ) {
                    Text(text = "Remove reminder")
                }

            }
        }
    }
}

private fun getCategoryId(categories: List<Category>, categoryName: String): Long {
    return categories.first { category -> category.name == categoryName }.id
}

@Composable
private fun CategoryListDropdown2(
    viewState: ReminderEditViewState,
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