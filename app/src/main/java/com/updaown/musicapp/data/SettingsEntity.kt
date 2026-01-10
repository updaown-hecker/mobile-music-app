package com.updaown.musicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1, // Single row for app settings
    
    // Playback
    val shuffleEnabled: Boolean = false,
    val repeatMode: Int = 0, // 0 = OFF, 1 = ONE, 2 = ALL
    val gaplessPlayback: Boolean = true,
    val crossfadeDuration: Int = 0, // 0-6 seconds
    
    // Audio
    val volumeNormalization: Boolean = false,
    val equalizerEnabled: Boolean = false,
    val equalizerPreset: String = "Normal", // Normal, Bass, Treble, Vocal, Classical, etc.
    val bass: Int = 0, // -10 to 10
    val treble: Int = 0, // -10 to 10
    val midrange: Int = 0, // -10 to 10
    
    // Theme
    val darkThemeEnabled: Boolean = true,
    val amoledTheme: Boolean = false, // Pure black for OLED
    
    // Notifications
    val showNotification: Boolean = true,
    val notificationOnLockScreen: Boolean = true,
    
    // Library
    val autoScanLibrary: Boolean = true,
    val cacheAlbumArt: Boolean = true,
    val sortOrder: String = "Title", // Title, Artist, Album, DateAdded
    
    // Sleep Timer
    val sleepTimerMinutes: Int = 0, // 0 = disabled
    
    // Other
    val hapticFeedback: Boolean = true,
    val showLyrics: Boolean = true,
    val audioVisualization: Boolean = true
)
