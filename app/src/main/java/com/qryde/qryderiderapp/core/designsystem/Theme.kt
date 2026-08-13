package com.qryde.qryderiderapp.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = QrydePrimary,
    onPrimary = QrydeOnPrimary,
    secondary = QrydeSecondary,
    background = QrydeBackground,
    onBackground = QrydeOnBackground,
    error = QrydeError
)

private val DarkColors = darkColorScheme(
    primary = QrydeSecondary,
    onPrimary = QrydeOnPrimary,
    secondary = QrydePrimary,
    error = QrydeError
)

@Composable
fun QrydeRiderTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        typography = QrydeTypography,
        content = content
    )
}
