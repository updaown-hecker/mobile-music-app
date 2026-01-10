package com.updaown.musicapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.updaown.musicapp.data.SettingsEntity
import com.updaown.musicapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val settings = viewModel.settings ?: SettingsEntity()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = AppleWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppleWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SamsungBlack
                )
            )
        },
        containerColor = SamsungBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Playback Settings
            SettingsSectionHeader("Playback")
            
            SettingsSwitchItem(
                icon = Icons.Default.Shuffle,
                title = "Shuffle",
                description = "Randomize playback order",
                isEnabled = settings.shuffleEnabled
            ) {
                viewModel.updateAllSettings(settings.copy(shuffleEnabled = it))
            }

            SettingsSwitchItem(
                icon = Icons.Default.Repeat,
                title = "Gapless Playback",
                description = "No silence between tracks",
                isEnabled = settings.gaplessPlayback
            ) {
                viewModel.updateAllSettings(settings.copy(gaplessPlayback = it))
            }

            SettingsSwitchItem(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = "Volume Normalization",
                description = "Equalize volume across tracks",
                isEnabled = settings.volumeNormalization
            ) {
                viewModel.updateAllSettings(settings.copy(volumeNormalization = it))
            }

            // Audio Settings
            SettingsSectionHeader("Audio")
            
            SettingsSwitchItem(
                icon = Icons.Default.Equalizer,
                title = "Equalizer",
                description = "Adjust audio quality",
                isEnabled = settings.equalizerEnabled
            ) {
                viewModel.updateEqualizerEnabled(it)
            }

            if (settings.equalizerEnabled) {
                EqualizerSettings(settings, viewModel)
            }

            SettingsSliderItem(
                icon = Icons.Default.Hearing,
                title = "Crossfade",
                description = "Smooth transition between songs",
                value = settings.crossfadeDuration,
                range = 0..6,
                unit = "s"
            ) {
                viewModel.updateAllSettings(settings.copy(crossfadeDuration = it))
            }

            // Display Settings
            SettingsSectionHeader("Display")
            
            SettingsSwitchItem(
                icon = Icons.Default.Brightness7,
                title = "Dark Theme",
                description = "Dark mode enabled",
                isEnabled = settings.darkThemeEnabled
            ) {
                viewModel.updateAllSettings(settings.copy(darkThemeEnabled = it))
            }

            SettingsSwitchItem(
                icon = Icons.Default.Contrast,
                title = "OLED Theme",
                description = "Pure black for OLED screens",
                isEnabled = settings.amoledTheme
            ) {
                viewModel.updateAllSettings(settings.copy(amoledTheme = it))
            }

            // Timer & Features
            SettingsSectionHeader("Features")
            
            SleepTimerSetting(settings, viewModel)

            SettingsSwitchItem(
                icon = Icons.Default.Vibration,
                title = "Haptic Feedback",
                description = "Vibration on actions",
                isEnabled = settings.hapticFeedback
            ) {
                viewModel.updateAllSettings(settings.copy(hapticFeedback = it))
            }

            SettingsSwitchItem(
                icon = Icons.Default.LibraryMusic,
                title = "Audio Visualization",
                description = "Show waveform during playback",
                isEnabled = settings.audioVisualization
            ) {
                viewModel.updateAllSettings(settings.copy(audioVisualization = it))
            }

            SettingsSwitchItem(
                icon = Icons.AutoMirrored.Filled.Notes,
                title = "Show Lyrics",
                description = "Display lyrics while playing",
                isEnabled = settings.showLyrics
            ) {
                viewModel.updateAllSettings(settings.copy(showLyrics = it))
            }

            // Notifications
            SettingsSectionHeader("Notifications")
            
            SettingsSwitchItem(
                icon = Icons.Default.Notifications,
                title = "Show Notification",
                description = "Display player notification",
                isEnabled = settings.showNotification
            ) {
                viewModel.updateAllSettings(settings.copy(showNotification = it))
            }

            SettingsSwitchItem(
                icon = Icons.Default.Lock,
                title = "Lock Screen Controls",
                description = "Show controls on lock screen",
                isEnabled = settings.notificationOnLockScreen
            ) {
                viewModel.updateAllSettings(settings.copy(notificationOnLockScreen = it))
            }

            // Library Settings
            SettingsSectionHeader("Library")
            
            SettingsSwitchItem(
                icon = Icons.Default.Sync,
                title = "Auto Scan Library",
                description = "Automatically detect new songs",
                isEnabled = settings.autoScanLibrary
            ) {
                viewModel.updateAllSettings(settings.copy(autoScanLibrary = it))
            }

            SettingsSwitchItem(
                icon = Icons.Default.Image,
                title = "Cache Album Art",
                description = "Store album artwork locally",
                isEnabled = settings.cacheAlbumArt
            ) {
                viewModel.updateAllSettings(settings.copy(cacheAlbumArt = it))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EqualizerSettings(settings: SettingsEntity, viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SamsungDarkGray)
            .padding(16.dp)
    ) {
        // Preset selector
        Text(
            "Preset",
            style = MaterialTheme.typography.labelMedium,
            color = AppleGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presets = listOf("Normal", "Bass", "Treble", "Vocal", "Classical", "Jazz", "Pop")
            presets.forEach { preset ->
                Button(
                    onClick = { viewModel.updateEqualizerPreset(preset) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (settings.equalizerPreset == preset) SamsungBlue else SamsungDarkGray.copy(alpha = 0.8f),
                        contentColor = AppleWhite
                    ),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(preset, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bass slider
        EqualizerSlider(
            label = "Bass",
            value = settings.bass,
            onValueChange = { viewModel.updateBass(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Midrange slider
        EqualizerSlider(
            label = "Midrange",
            value = settings.midrange,
            onValueChange = { viewModel.updateMidrange(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Treble slider
        EqualizerSlider(
            label = "Treble",
            value = settings.treble,
            onValueChange = { viewModel.updateTreble(it) }
        )
    }
}

@Composable
private fun EqualizerSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = AppleGray)
            Text("$value", style = MaterialTheme.typography.labelSmall, color = AppleWhite)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = -10f..10f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = SamsungBlue,
                activeTrackColor = SamsungBlue,
                inactiveTrackColor = SamsungDarkGray
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SleepTimerSetting(settings: SettingsEntity, viewModel: MainViewModel) {
    var showTimerDialog by remember { mutableStateOf(false) }
    val timerOptions = listOf(0, 5, 10, 15, 30, 45, 60)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showTimerDialog = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Timer,
            contentDescription = null,
            tint = SamsungBlue,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Sleep Timer", color = AppleWhite, style = MaterialTheme.typography.bodyLarge)
            Text(
                if (settings.sleepTimerMinutes > 0) "${settings.sleepTimerMinutes} minutes" else "Off",
                color = AppleGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AppleGray)
    }

    if (showTimerDialog) {
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            containerColor = SamsungDarkGray,
            title = { Text("Sleep Timer", color = AppleWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timerOptions.forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.updateSleepTimer(minutes)
                                    showTimerDialog = false
                                }
                                .background(
                                    if (settings.sleepTimerMinutes == minutes) SamsungBlue.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.sleepTimerMinutes == minutes,
                                onClick = {
                                    viewModel.updateSleepTimer(minutes)
                                    showTimerDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = SamsungBlue,
                                    unselectedColor = AppleGray
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                if (minutes == 0) "Off" else "$minutes minutes",
                                color = AppleWhite
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = SamsungBlue,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isEnabled) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = SamsungBlue,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = AppleWhite, style = MaterialTheme.typography.bodyLarge)
            Text(description, color = AppleGray, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AppleWhite,
                checkedTrackColor = SamsungBlue,
                uncheckedThumbColor = AppleGray,
                uncheckedTrackColor = SamsungDarkGray
            )
        )
    }
}

@Composable
private fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    description: String,
    value: Int,
    range: IntRange,
    unit: String = "",
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = SamsungBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = AppleWhite, style = MaterialTheme.typography.bodyLarge)
                Text(description, color = AppleGray, style = MaterialTheme.typography.bodySmall)
            }
            Text("$value$unit", color = AppleWhite, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.count() - 2,
            colors = SliderDefaults.colors(
                thumbColor = SamsungBlue,
                activeTrackColor = SamsungBlue,
                inactiveTrackColor = SamsungDarkGray
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
