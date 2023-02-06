package com.jukvau.reminderapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController



class reminderappAppState(
    val navController: NavHostController
) {
    fun navigateBack() {
        navController.popBackStack()
    }
}

@Composable
fun rememberreminderappAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {
    reminderappAppState(navController)
}