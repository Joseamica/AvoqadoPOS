@file:OptIn(ExperimentalPermissionsApi::class)

package com.avoqado.pos.core.presentation.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestPermissions(
    accessRevokedDialogOnDismissRequest: () -> Unit = {},
    onPermissionResult: (Boolean) -> Unit = {}
) {
    var showDialogIfAccessRevoked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val permissionState: MultiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE
        )
    )

    ObserverLifecycleEvents(
        onResume = {
            if (!permissionState.allPermissionsGranted) {
                permissionState.launchMultiplePermissionRequest()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        showDialogIfAccessRevoked = true
                    } else {
                        showDialogIfAccessRevoked = false
                        onPermissionResult(true)
                    }
                } else {
                    showDialogIfAccessRevoked = false
                    onPermissionResult(true)
                }
            }
        }
    )



    if (showDialogIfAccessRevoked) {
        var isVisible by remember { mutableStateOf(true) }
        if (isVisible) {
            BasicAlertDialog(
                onDismissRequest = {
                    isVisible = false
                    accessRevokedDialogOnDismissRequest()
                },
                content = {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(0.dp),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 42.dp),
                                text = "Por favor permitir acceso a todos los permisos para poder continuar",
                                textAlign = TextAlign.Center,
                            )

                            Button(
                                onClick = {

                                    val intent =
                                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                            data = Uri.parse("package:${context.packageName}")
                                        }
                                    activity?.startActivity(intent)

                                }
                            ) {
                                Text("Ok")
                            }
                        }
                    }
                }
            )
        }
    }
}