package com.jukvau.reminderapp.ui.home.categoryReminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jukvau.reminderapp.R
import com.jukvau.reminderapp.data.entity.Category
import com.jukvau.reminderapp.data.entity.Reminder
import com.jukvau.reminderapp.data.room.ReminderToCategory
import com.jukvau.reminderapp.ui.reminder.ReminderViewModel
import com.jukvau.reminderapp.util.viewModelProviderFactoryOf
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CategoryReminder(
    categoryId: Long,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel: CategoryReminderViewModel = viewModel(
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf { CategoryReminderViewModel(categoryId) }
    )
    val viewState by viewModel.state.collectAsState()

    Column(modifier = modifier) {
        ReminderList(
            list = viewState.reminders,
            navController = navController
        )
    }
}

@Composable
private fun ReminderList(
    list: List<ReminderToCategory>,
    navController: NavController
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(list) { item ->
            ReminderListItem(
                reminder = item.reminder,
                category = item.category,
                onClick = {},
                modifier = Modifier.fillParentMaxWidth(),
                navController = navController
            )
        }
    }
}

@Composable
private fun ReminderListItem(
    reminder: Reminder,
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ReminderViewModel = viewModel(),
    reminder_id: Long = reminder.reminderId
) {
    ConstraintLayout(modifier = modifier.clickable { onClick() }) {
        val (divider, reminderMessage, reminderCategory, icon, date) = createRefs()
        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )

        // title
        Text(
            text = reminder.reminderMessage,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(reminderMessage) {
                linkTo(
                    start = parent.start,
                    end = icon.start,
                    startMargin = 24.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )

        // category
        Text(
            text = category.name,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.constrainAs(reminderCategory) {
                linkTo(
                    start = parent.start,
                    end = icon.start,
                    startMargin = 24.dp,
                    endMargin = 8.dp,
                    bias = 0f
                )
                top.linkTo(reminderMessage.bottom, margin = 6.dp)
                bottom.linkTo(parent.bottom, 10.dp)
                width = Dimension.preferredWrapContent
            }
        )

        // date
        Text(
//            text = when {
//                reminder.reminderDate != null -> { reminder.reminderDate.formatToString() }
//                else -> Date().formatToString()
//            },
            text = reminder.reminderTime.toDateString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(date) {
                linkTo(
                    start = reminderCategory.end,
                    end = icon.start,
                    startMargin = 8.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                centerVerticallyTo(reminderCategory)
                top.linkTo(reminderMessage.bottom, 6.dp)
                bottom.linkTo(parent.bottom, 18.dp)
            }
        )

        // icon

        IconButton(
            onClick = {
//                val ReminderId = Reminder.reminderId
                navController.navigate("reminderedit/${reminder.reminderId}")
            },
            modifier = Modifier
                .size(50.dp)
                .padding(6.dp)
                .constrainAs(icon) {
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(parent.end)
                }
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(R.string.Edit)
            )
        }
    }
}

private fun Date.formatToString(): String {
    return SimpleDateFormat( "HH:mm:ss", Locale.getDefault()).format(this)
}

private fun Long.toDateString(): String {
    return SimpleDateFormat( "HH:mm:ss", Locale.getDefault()).format(Date(this))
}