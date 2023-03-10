package com.jukvau.reminderapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jukvau.reminderapp.rememberreminderappAppState
import com.jukvau.reminderapp.reminderappAppState
import com.jukvau.reminderapp.ui.help.TutorialScreen
import com.jukvau.reminderapp.ui.home.Home
import com.jukvau.reminderapp.ui.login.LoginScreen
import com.jukvau.reminderapp.ui.maps.ReminderLocationMap
import com.jukvau.reminderapp.ui.profile.ProfileScreen
import com.jukvau.reminderapp.ui.reminder.Reminder
import com.jukvau.reminderapp.ui.reminder.ReminderEdit

@Composable
fun ReminderApp(
    appState: reminderappAppState = rememberreminderappAppState(),
    //navController: NavController
) {
    NavHost(
        navController = appState.navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
//            LoginScreen(navController = appState.navController)
            LoginScreen(
                modifier = Modifier.fillMaxSize(),
                navController = appState.navController
            )
        }
        composable(route = "home") {
            Home(
                navController = appState.navController
            )
        }
        composable(route = "reminder") {
            Reminder(onBackPress = appState::navigateBack, navController = appState.navController)
        }
        composable(route = "profile") {
            ProfileScreen(onBackPress = appState::navigateBack)
        }
        composable(route = "help") {
            TutorialScreen(onBackPress = appState::navigateBack)
        }
        composable("reminderedit/{reminderId}") {
            backStackEntry -> ReminderEdit(onBackPress = appState::navigateBack, backStackEntry.arguments?.getString("reminderId"), navController = appState.navController)
        }

        composable(route = "map") {
            ReminderLocationMap(navController = appState.navController)
        }
    }
}