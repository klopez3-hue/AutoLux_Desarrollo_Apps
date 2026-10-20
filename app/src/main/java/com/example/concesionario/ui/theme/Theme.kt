
package com.example.concesionario.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SportRed,
    onPrimary = White,
    secondary = SportRedLight,
    onSecondary = Black,
    tertiary = LightGray,
    onTertiary = Black,
    background = Black,
    onBackground = TextPrimary,
    surface = Graphite,
    onSurface = TextPrimary,
    surfaceVariant = DarkGray,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = SportRed,
    onPrimary = White,
    secondary = SportRedDark,
    onSecondary = White,
    tertiary = Graphite,
    onTertiary = White,
    background = OffWhite,
    onBackground = TextDark,
    surface = White,
    onSurface = TextDark,
    surfaceVariant = LightGray,
    onSurfaceVariant = DarkGray,
    error = ErrorRed,
    onError = White
)

@Composable
fun ConcesionarioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
