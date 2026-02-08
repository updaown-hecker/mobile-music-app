package com.updaown.musicapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.unit.sp
import com.updaown.musicapp.data.SettingsEntity
import com.updaown.musicapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val settings = viewModel.settings ?: SettingsEntity()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppleSystemBlue)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = AppleCharcoal,
                    titleContentColor = AppleWhite,
                    scrolledContainerColor = AppleCharcoal
                )
            )
        },
        containerColor = AppleCharcoal
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsGroup("PLAYBACK") {
                SettingsSwitchItem(Icons.Default.Shuffle, "Shuffle", settings.shuffleEnabled, AppleSystemBlue) {
                    viewModel.updateAllSettings(settings.copy(shuffleEnabled = it))
                }
                SettingsModeItem("Repeat", listOf("Off", "One", "All")[settings.repeatMode], AppleSystemBlue) {
                    val next = (settings.repeatMode + 1) % 3
                    viewModel.updateAllSettings(settings.copy(repeatMode = next))
                }
                SettingsSliderItem(Icons.Default.Speed, "Playback Speed", settings.playbackSpeed, 0.5f..2.0f, "x", AppleSystemOrange) {
                    viewModel.updateAllSettings(settings.copy(playbackSpeed = it))
                }
                SettingsSwitchItem(Icons.Default.GraphicEq, "Skip Silence", settings.skipSilence, AppleSystemGreen) {
                    viewModel.updateAllSettings(settings.copy(skipSilence = it))
                }
                SettingsSliderItem(Icons.Default.Hearing, "Crossfade", settings.crossfadeDuration.toFloat(), 0f..6f, "s", AppleSystemPink) {
                    viewModel.updateAllSettings(settings.copy(crossfadeDuration = it.toInt()))
                }
                SettingsSwitchItem(Icons.Default.Repeat, "Gapless Playback", settings.gaplessPlayback, AppleSystemIndigo) {
                    viewModel.updateAllSettings(settings.copy(gaplessPlayback = it))
                }
            }

            SettingsGroup("AUDIO") {
                SettingsSwitchItem(Icons.AutoMirrored.Filled.VolumeUp, "Volume Normalization", settings.volumeNormalization, AppleSystemTeal) {
                    viewModel.updateAllSettings(settings.copy(volumeNormalization = it))
                }
                SettingsSwitchItem(Icons.Default.Equalizer, "Equalizer", settings.equalizerEnabled, AppleSystemPurple) {
                    viewModel.updateEqualizerEnabled(it)
                }
                if (settings.equalizerEnabled) {
                    EqualizerControls(settings, viewModel)
                }
            }

            SettingsGroup("FEATURES") {
                SettingsModeItem("Sleep Timer", if (settings.sleepTimerMinutes > 0) "${settings.sleepTimerMinutes}m" else "Off", AppleSystemOrange) {
                    val options = listOf(0, 5, 10, 15, 30, 45, 60)
                    val nextIndex = (options.indexOf(settings.sleepTimerMinutes) + 1) % options.size
                    viewModel.updateSleepTimer(options[nextIndex])
                }
                SettingsSwitchItem(Icons.Default.Vibration, "Haptic Feedback", settings.hapticFeedback, AppleSystemPink) {
                    viewModel.updateAllSettings(settings.copy(hapticFeedback = it))
                }
            }

            SettingsGroup("DISPLAY") {
                SettingsSwitchItem(Icons.Default.Brightness7, "Dark Theme", settings.darkThemeEnabled, AppleGray) {
                    viewModel.updateAllSettings(settings.copy(darkThemeEnabled = it))
                }
                SettingsSwitchItem(Icons.Default.Contrast, "OLED Theme", settings.amoledTheme, Color.Black) {
                    viewModel.updateAllSettings(settings.copy(amoledTheme = it))
                }
            }

            SettingsGroup("LIBRARY") {
                SettingsModeItem("Sort Order", settings.sortOrder, AppleSystemBlue) {
                    val orders = listOf("Title", "Artist", "Album", "DateAdded")
                    val nextIndex = (orders.indexOf(settings.sortOrder) + 1) % orders.size
                    viewModel.updateAllSettings(settings.copy(sortOrder = orders[nextIndex]))
                }
                SettingsSwitchItem(Icons.Default.Sync, "Auto Scan Library", settings.autoScanLibrary, AppleSystemBlue) {
                    viewModel.updateAllSettings(settings.copy(autoScanLibrary = it))
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = AppleGray,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppleGraphite)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsSwitchItem(icon: ImageVector, title: String, isEnabled: Boolean, iconColor: Color, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(iconColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = AppleWhite, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = AppleWhite, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AppleWhite,
                checkedTrackColor = AppleSystemGreen,
                uncheckedThumbColor = AppleWhite,
                uncheckedTrackColor = AppleSlate
            )
        )
    }
}

@Composable
fun SettingsModeItem(title: String, current: String, iconColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(iconColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Tune, null, tint = AppleWhite, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = AppleWhite, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Text(current, color = AppleGray, style = MaterialTheme.typography.bodyLarge)
        Icon(Icons.Default.ChevronRight, null, tint = AppleSlate, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun SettingsSliderItem(icon: ImageVector, title: String, value: Float, range: ClosedFloatingPointRange<Float>, unit: String, iconColor: Color, onValueChange: (Float) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = AppleWhite, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(title, color = AppleWhite, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Text(String.format("%.1f%s", value, unit), color = AppleGray, style = MaterialTheme.typography.bodyLarge)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = AppleWhite,
                activeTrackColor = AppleSystemBlue,
                inactiveTrackColor = AppleSlate
            )
        )
    }
}

@Composable
fun EqualizerControls(settings: SettingsEntity, viewModel: MainViewModel) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text("PRESETS", style = MaterialTheme.typography.labelSmall, color = AppleGray)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val presets = listOf("Normal", "Bass", "Treble", "Vocal", "Classical", "Jazz", "Pop")
            presets.forEach { preset ->
                FilterChip(
                    selected = settings.equalizerPreset == preset,
                    onClick = { viewModel.updateEqualizerPreset(preset) },
                    label = { Text(preset) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppleSystemBlue,
                        selectedLabelColor = AppleWhite,
                        labelColor = AppleGray
                    )
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        EqualizerSliderRow("Bass", settings.bass) { viewModel.updateBass(it) }
        EqualizerSliderRow("Midrange", settings.midrange) { viewModel.updateMidrange(it) }
        EqualizerSliderRow("Treble", settings.treble) { viewModel.updateTreble(it) }
    }
}

@Composable
fun EqualizerSliderRow(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = AppleWhite, style = MaterialTheme.typography.bodyMedium)
            Text(value.toString(), color = AppleGray, style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = -10f..10f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = AppleWhite,
                activeTrackColor = AppleSystemBlue,
                inactiveTrackColor = AppleSlate
            )
        )
    }
}
