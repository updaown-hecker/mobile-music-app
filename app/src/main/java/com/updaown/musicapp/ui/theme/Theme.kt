package com.updaown.musicapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
        darkColorScheme(
                primary = SamsungBlue,
                secondary = AppleGray,
                tertiary = AppleSystemPink,
                background = SamsungBlack,
                surface = SamsungBlack, // Deep black background
                onPrimary = AppleWhite,
                onSecondary = AppleWhite,
                onTertiary = AppleWhite,
                onBackground = AppleWhite,
                onSurface = AppleWhite,
                surfaceVariant = SamsungDarkGray // For cards
        )

@Composable
fun MusicAppTheme(
        darkTheme: Boolean = true, // Force Dark Mode
        // Dynamic color is available on Android 12+
        _dynamicColor: Boolean = false, // Disable dynamic color
        content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Always use dark scheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
