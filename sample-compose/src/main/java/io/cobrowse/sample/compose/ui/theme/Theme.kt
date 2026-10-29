package io.cobrowse.sample.compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = AccentColor,
    tertiary = Pink80,
    background = PrimaryBackground,
    surface = SecondaryBackground,
    onPrimary = SecondaryTextColor,
    onSecondary = PrimaryTextColor,
    onTertiary = SecondaryTextColor,
    onBackground = PrimaryTextColor,
    onSurface = PrimaryTextColor,
    // The secondary text color of the View-based sample's theme
    onSurfaceVariant = Color(0x8A000000),
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = AccentColor,
    tertiary = Pink80,
    background = PrimaryBackgroundDark,
    surface = SecondaryBackgroundDark,
    onPrimary = SecondaryTextColor,
    onSecondary = PrimaryTextColor,
    onTertiary = SecondaryTextColor,
    onBackground = SecondaryTextColor,
    onSurface = SecondaryTextColor,
    onSurfaceVariant = Color(0xB3FFFFFF),
)

@Composable
fun SampleComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
