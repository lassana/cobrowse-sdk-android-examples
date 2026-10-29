package io.cobrowse.sample.compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
