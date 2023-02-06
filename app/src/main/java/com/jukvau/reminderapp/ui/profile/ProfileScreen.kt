package com.jukvau.reminderapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.jukvau.reminderapp.datastore.StoreUserData
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onBackPress: () -> Unit
) {
    // context
    val context = LocalContext.current
    // scope
    val scope = rememberCoroutineScope()
    // datastore UserData
    val dataStore = StoreUserData(context)
    // get saved username
    val savedUserName = dataStore.getUserName.collectAsState(initial = "")
    // get saved password
    val savedPassWord = dataStore.getPassWord.collectAsState(initial = "")


    val usernameNew = remember { mutableStateOf("") }
    val passwordNew = remember { mutableStateOf("") }

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
                Text(text = "Profile")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)
            ) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = usernameNew.value,
                    onValueChange = { usernamevalue -> usernameNew.value = usernamevalue },
                    label = { Text(text = "New Username") },
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = passwordNew.value,
                    onValueChange = { passwordvalue -> passwordNew.value = passwordvalue },
                    label = { Text(text = "New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { scope.launch { dataStore.saveUsername(usernameNew.value)
                                scope.launch { dataStore.savePassword(passwordNew.value) }} },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                ) {
                    Text(text = "Save new Login details")
                }
            }
        }
    }
}