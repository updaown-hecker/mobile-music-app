package com.updaown.musicapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
    darkColorScheme(
        primary = SamsungBlue,
        secondary = AppleGray,
        tertiary = AppleSystemPink,
        background = SamsungBlack,
        surface = SamsungBlack,
        onPrimary = AppleWhite,
        onSecondary = AppleWhite,
        onTertiary = AppleWhite,
        onBackground = AppleWhite,
        onSurface = AppleWhite,
        surfaceVariant = SamsungDarkGray
    )

private val AmoledDarkColorScheme =
    darkColorScheme(
        primary = SamsungBlue,
        secondary = AppleGray,
        tertiary = AppleSystemPink,
        background = androidx.compose.ui.graphics.Color.Black,
        surface = androidx.compose.ui.graphics.Color.Black,
        onPrimary = AppleWhite,
        onSecondary = AppleWhite,
        onTertiary = AppleWhite,
        onBackground = AppleWhite,
        onSurface = AppleWhite,
        surfaceVariant = SamsungDarkGray
    )

private val LightColorScheme =
    lightColorScheme(
        primary = AppleSystemBlue,
        secondary = AppleGray,
        background = AppleLightGray,
        surface = AppleWhite,
        onPrimary = AppleWhite,
        onSecondary = AppleDarkGray,
        onBackground = AppleDarkGray,
        onSurface = AppleDarkGray,
        surfaceVariant = Color(0xFFE8E8ED)
    )

@Composable
fun MusicAppTheme(
    darkTheme: Boolean = true,
    amoledTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        when {
            darkTheme && amoledTheme -> AmoledDarkColorScheme
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

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
