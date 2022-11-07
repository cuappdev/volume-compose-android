package com.cornellappdev.volume.ui.components.general

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.lato
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Creates a Permission request dialog for requesting POST_NOTIFICATIONS permission.
 *
 * We only need to request the permissions if the Android phone is on version 13 or above.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestDialog(
    showBottomBar: MutableState<Boolean>,
    notificationFlowStatus: Boolean,
    updateNotificationFlowStatus: (Boolean) -> Unit
) {
    var requestingPermission by remember { mutableStateOf(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) }
    showBottomBar.value = !requestingPermission

    AnimatedVisibility(
        visible = requestingPermission,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val context = LocalContext.current

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionState =
                rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

            when (val permissionStatus = notificationPermissionState.status) {
                is PermissionStatus.Granted -> {
                    requestingPermission = false
                }
                is PermissionStatus.Denied -> {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(horizontal = 20.dp),
                                elevation = 10.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(33.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Notifications are necessary to send you " +
                                                "updates about new articles and magazines by publishers you follow and the Weekly Debrief! " +
                                                if (permissionStatus.shouldShowRationale || !notificationFlowStatus) {
                                                    ""
                                                } else {
                                                    "\n\nPlease click the button below to go to the settings to enable notifications."
                                                },
                                        textAlign = TextAlign.Center,
                                        fontFamily = lato,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Button(
                                        onClick = {
                                            if (permissionStatus.shouldShowRationale || !notificationFlowStatus) {
                                                notificationPermissionState.launchPermissionRequest()
                                                updateNotificationFlowStatus(true)
                                            } else {
                                                context.openSettings()
                                            }
                                            requestingPermission = false
                                        },
                                        shape = RoundedCornerShape(5.dp),
                                        colors = ButtonDefaults.buttonColors(backgroundColor = VolumeOrange),
                                    ) {
                                        Text(
                                            text = if (permissionStatus.shouldShowRationale || !notificationFlowStatus) {
                                                "Request Permission"
                                            } else {
                                                "Open Settings"
                                            },
                                            color = Color.White,
                                            fontFamily = lato
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Context.openSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}
