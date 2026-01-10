package com.updaown.musicapp.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.updaown.musicapp.ui.theme.AppleSystemBlue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

enum class Screen {
    Library,
    Import,
    Settings
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusicApp() {
    val viewModel: MainViewModel = viewModel()
    var currentScreen by remember { mutableStateOf(Screen.Library) }
    val backStack = remember { mutableStateListOf<Screen>() }

    // Define permissions needed - only critical ones for music playback
    val permissions = buildList {
        if (Build.VERSION.SDK_INT >= 33) {
            add(Manifest.permission.READ_MEDIA_AUDIO)
            add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    val permissionState = rememberMultiplePermissionsState(permissions)

    // Auto-launch request on start if not granted
    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    // Update VM when status changes
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            viewModel.permissionGranted = true
            viewModel.loadSongs()
        }
    }

    if (viewModel.permissionGranted || permissionState.allPermissionsGranted) {
        // Handle system back button
        BackHandler(enabled = backStack.isNotEmpty()) {
            if (backStack.isNotEmpty()) {
                currentScreen = backStack.removeAt(backStack.size - 1)
            }
        }

        when (currentScreen) {
            Screen.Library ->
                    MainScreen(
                            viewModel = viewModel,
                            onNavigateToImport = { 
                                backStack.add(currentScreen)
                                currentScreen = Screen.Import 
                            },
                            onNavigateToSettings = { 
                                backStack.add(currentScreen)
                                currentScreen = Screen.Settings 
                            }
                    )
            Screen.Import ->
                    ImportScreen(viewModel = viewModel, onBack = { 
                        if (backStack.isNotEmpty()) {
                            currentScreen = backStack.removeAt(backStack.size - 1)
                        } else {
                            currentScreen = Screen.Library
                        }
                    })
            Screen.Settings ->
                    SettingsScreen(viewModel = viewModel, onBack = { 
                        if (backStack.isNotEmpty()) {
                            currentScreen = backStack.removeAt(backStack.size - 1)
                        } else {
                            currentScreen = Screen.Library
                        }
                    })
        }
    } else {
        // "Apple-style" Permission Request Screen
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
            ) {
                Text(
                        text = "Music Access Required",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                        text =
                                "To play your local songs, please allow access to audio files on your device.",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                        onClick = { permissionState.launchMultiplePermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = AppleSystemBlue)
                ) { Text("Grant Access") }
            }
        }
    }
}
