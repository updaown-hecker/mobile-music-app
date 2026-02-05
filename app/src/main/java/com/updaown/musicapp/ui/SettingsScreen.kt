package com.updaown.musicapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.updaown.musicapp.data.SettingsEntity
import com.updaown.musicapp.ui.theme.AppleGray
import com.updaown.musicapp.ui.theme.AppleWhite
import com.updaown.musicapp.ui.theme.SamsungBlack
import com.updaown.musicapp.ui.theme.SamsungBlue
import com.updaown.musicapp.ui.theme.SamsungDarkGray
import com.updaown.musicapp.ui.theme.SamsungLightGray

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val settings = viewModel.settings ?: SettingsEntity()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold, color = AppleWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppleWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SamsungBlack)
            )
        },
        containerColor = SamsungBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsSection("Playback") {
                SettingsSwitchItem(Icons.Default.Shuffle, "Shuffle", "Randomize playback order", settings.shuffleEnabled) {
                    viewModel.updateAllSettings(settings.copy(shuffleEnabled = it))
                }
                SettingsModeChips("Repeat", settings.repeatMode, listOf("Off", "One", "All")) {
                    viewModel.updateAllSettings(settings.copy(repeatMode = it))
                }
                SettingsSliderItem(Icons.Default.Speed, "Playback Speed", "0.5x to 2.0x", settings.playbackSpeed, 0.5f..2f, "x") {
                    viewModel.updateAllSettings(settings.copy(playbackSpeed = it))
                }
                SettingsSwitchItem(Icons.Default.GraphicEq, "Skip Silence", "Automatically skip silent segments", settings.skipSilence) {
                    viewModel.updateAllSettings(settings.copy(skipSilence = it))
                }
                SettingsSliderItem(Icons.Default.Hearing, "Crossfade", "Smooth transition between songs", settings.crossfadeDuration.toFloat(), 0f..6f, "s") {
                    viewModel.updateAllSettings(settings.copy(crossfadeDuration = it.toInt()))
                }
                SettingsSwitchItem(Icons.Default.Repeat, "Gapless Playback", "No silence between tracks", settings.gaplessPlayback) {
                    viewModel.updateAllSettings(settings.copy(gaplessPlayback = it))
                }
            }

            SettingsSection("Audio") {
                SettingsSwitchItem(Icons.AutoMirrored.Filled.VolumeUp, "Volume Normalization", "Equalize volume across tracks", settings.volumeNormalization) {
                    viewModel.updateAllSettings(settings.copy(volumeNormalization = it))
                }
                SettingsSwitchItem(Icons.Default.Equalizer, "Equalizer", "Adjust audio quality", settings.equalizerEnabled) {
                    viewModel.updateEqualizerEnabled(it)
                }
                if (settings.equalizerEnabled) EqualizerSettings(settings, viewModel)
            }

            SettingsSection("Display") {
                SettingsSwitchItem(Icons.Default.Brightness7, "Dark Theme", "Dark mode enabled", settings.darkThemeEnabled) {
                    viewModel.updateAllSettings(settings.copy(darkThemeEnabled = it))
                }
                SettingsSwitchItem(Icons.Default.Contrast, "OLED Theme", "Pure black for OLED screens", settings.amoledTheme) {
                    viewModel.updateAllSettings(settings.copy(amoledTheme = it))
                }
            }

            SettingsSection("Features") {
                SleepTimerSetting(settings, viewModel)
                SettingsSwitchItem(Icons.Default.Vibration, "Haptic Feedback", "Vibration on actions", settings.hapticFeedback) {
                    viewModel.updateAllSettings(settings.copy(hapticFeedback = it))
                }
                SettingsSwitchItem(Icons.Default.LibraryMusic, "Audio Visualization", "Show waveform during playback", settings.audioVisualization) {
                    viewModel.updateAllSettings(settings.copy(audioVisualization = it))
                }
                SettingsSwitchItem(Icons.AutoMirrored.Filled.Notes, "Show Lyrics", "Display lyrics while playing", settings.showLyrics) {
                    viewModel.updateAllSettings(settings.copy(showLyrics = it))
                }
            }

            SettingsSection("Notifications") {
                SettingsSwitchItem(Icons.Default.Notifications, "Show Notification", "Display player notification", settings.showNotification) {
                    viewModel.updateAllSettings(settings.copy(showNotification = it))
                }
                SettingsSwitchItem(Icons.Default.Lock, "Lock Screen Controls", "Show controls on lock screen", settings.notificationOnLockScreen) {
                    viewModel.updateAllSettings(settings.copy(notificationOnLockScreen = it))
                }
            }

            SettingsSection("Library") {
                SortOrderSetting(settings) { viewModel.updateAllSettings(settings.copy(sortOrder = it)) }
                SettingsSwitchItem(Icons.Default.Sync, "Auto Scan Library", "Automatically detect new songs", settings.autoScanLibrary) {
                    viewModel.updateAllSettings(settings.copy(autoScanLibrary = it))
                }
                SettingsSwitchItem(Icons.Default.Image, "Cache Album Art", "Store album artwork locally", settings.cacheAlbumArt) {
                    viewModel.updateAllSettings(settings.copy(cacheAlbumArt = it))
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SamsungDarkGray.copy(alpha = 0.95f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = SamsungBlue, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(2.dp))
        content()
    }
}

@Composable
private fun SettingsModeChips(label: String, current: Int, options: List<String>, onSelect: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = AppleWhite, style = MaterialTheme.typography.bodyLarge)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { index, text ->
                Button(
                    onClick = { onSelect(index) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (current == index) SamsungBlue else SamsungLightGray,
                        contentColor = AppleWhite
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) { Text(text) }
            }
        }
    }
}

@Composable
private fun EqualizerSettings(settings: SettingsEntity, viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SamsungLightGray.copy(alpha = 0.9f))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val presets = listOf("Normal", "Bass", "Treble", "Vocal", "Classical", "Jazz", "Pop")
            presets.forEach { preset ->
                Button(
                    onClick = { viewModel.updateEqualizerPreset(preset) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (settings.equalizerPreset == preset) SamsungBlue else SamsungDarkGray,
                        contentColor = AppleWhite
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) { Text(preset) }
            }
        }
        EqualizerSlider("Bass", settings.bass, viewModel::updateBass)
        EqualizerSlider("Midrange", settings.midrange, viewModel::updateMidrange)
        EqualizerSlider("Treble", settings.treble, viewModel::updateTreble)
    }
}

@Composable
private fun EqualizerSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(Modifier.padding(top = 10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = AppleGray)
            Text(value.toString(), color = AppleWhite)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = -10f..10f,
            steps = 19,
            colors = SliderDefaults.colors(thumbColor = SamsungBlue, activeTrackColor = SamsungBlue, inactiveTrackColor = SamsungDarkGray)
        )
    }
}

@Composable
private fun SortOrderSetting(settings: SettingsEntity, onSortChange: (String) -> Unit) {
    val options = listOf("Title", "Artist", "Album", "DateAdded")
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("Sort by", color = AppleWhite, style = MaterialTheme.typography.bodyLarge)
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                Button(
                    onClick = { onSortChange(option) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (settings.sortOrder == option) SamsungBlue else SamsungLightGray,
                        contentColor = AppleWhite
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) { Text(option, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
private fun SettingsSwitchItem(icon: ImageVector, title: String, description: String, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle(!isEnabled) }
            .background(SamsungLightGray.copy(alpha = 0.42f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(SamsungBlue.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = SamsungBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = AppleWhite, style = MaterialTheme.typography.bodyLarge)
            Text(description, color = AppleGray, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = AppleWhite, checkedTrackColor = SamsungBlue)
        )
    }
}

@Composable
private fun SettingsSliderItem(icon: ImageVector, title: String, description: String, value: Float, range: ClosedFloatingPointRange<Float>, unit: String = "", onValueChange: (Float) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SamsungLightGray.copy(alpha = 0.42f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = SamsungBlue, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = AppleWhite)
                Text(description, color = AppleGray, style = MaterialTheme.typography.bodySmall)
            }
            Text(String.format("%.1f%s", value, unit), color = AppleWhite)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(thumbColor = SamsungBlue, activeTrackColor = SamsungBlue, inactiveTrackColor = SamsungDarkGray)
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
            .clip(RoundedCornerShape(16.dp))
            .clickable { showTimerDialog = true }
            .background(SamsungLightGray.copy(alpha = 0.42f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Timer, contentDescription = null, tint = SamsungBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Sleep Timer", color = AppleWhite)
            Text(if (settings.sleepTimerMinutes > 0) "${settings.sleepTimerMinutes} minutes" else "Off", color = AppleGray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AppleGray)
    }

    if (showTimerDialog) {
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            containerColor = SamsungDarkGray,
            title = { Text("Sleep Timer", color = AppleWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    timerOptions.forEach { minutes ->
                        Row(modifier = Modifier.fillMaxWidth().clickable {
                            viewModel.updateSleepTimer(minutes)
                            showTimerDialog = false
                        }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = settings.sleepTimerMinutes == minutes,
                                onClick = {
                                    viewModel.updateSleepTimer(minutes)
                                    showTimerDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = SamsungBlue, unselectedColor = AppleGray)
                            )
                            Text(if (minutes == 0) "Off" else "$minutes minutes", color = AppleWhite)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
