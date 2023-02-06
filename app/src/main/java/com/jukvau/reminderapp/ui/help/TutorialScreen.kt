package com.jukvau.reminderapp.ui.help

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.jukvau.reminderapp.datastore.StoreUserData
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    onBackPress: () -> Unit
) {
    var helptext by remember { mutableStateOf("Press the Floating Plus Button to create a reminder") }
    var help = 1
    var helpIcon by remember { mutableStateOf(Icons.Default.Add) }
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
                Text(text = "Tutorial")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)
            ) {

                Text(text = helptext)
                Spacer(modifier = Modifier.height(5.dp))
                Image(imageVector = helpIcon, contentDescription = null, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { if(help == 1) { helptext = "Use the Log-Out Button in the top app bar to log out"; help = 2; helpIcon = Icons.Default.ExitToApp}
                                else if(help == 2) { helptext = "Use the Profile Button in the top app bar to change login details"; help = 3; helpIcon = Icons.Default.AccountCircle}
                                else if(help == 3) { helptext = "Press the Floating Plus Button to create a reminder"; help = 1; helpIcon = Icons.Default.Add}},
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}