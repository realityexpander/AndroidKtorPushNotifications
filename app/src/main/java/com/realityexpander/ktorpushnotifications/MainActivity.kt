package com.realityexpander.ktorpushnotifications

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.onesignal.OSPermissionObserver
import com.onesignal.OSPermissionStateChanges
import com.onesignal.OneSignal
import com.realityexpander.ktorpushnotifications.data.remote.ApiServiceImpl
import com.realityexpander.ktorpushnotifications.ui.theme.KtorPushNotificationsTheme
import io.ktor.client.*
import io.ktor.client.engine.android.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), OSPermissionObserver {

    private val client = HttpClient(Android)
    private val service = ApiServiceImpl(client)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        OneSignal.promptForPushNotifications()
        OneSignal.addPermissionObserver(this)

        setContent {
            KtorPushNotificationsTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top,
                ) {
                    val scope = rememberCoroutineScope()
                    var title by remember {
                        mutableStateOf("")
                    }
                    var description by remember {
                        mutableStateOf("")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onBackground),
                        placeholder = {
                            Text("Title")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onBackground),
                        placeholder = {
                            Text("Description")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Send Notification
                    Button(
                        onClick = {
                            scope.launch {
                                service.sendNotification(
                                    title = title,
                                    description = description
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Send")
                    }
                }
            }
        }
    }

    override fun onOSPermissionChanged(stateChanges: OSPermissionStateChanges) {
        if (stateChanges.from.areNotificationsEnabled() &&
            !stateChanges.to.areNotificationsEnabled()
        ) {

            AlertDialog.Builder(this)
                .setMessage("Notifications Disabled!")
                .show();
        }

        Log.i("Debug", "onOSPermissionChanged: " + stateChanges);
    }


}