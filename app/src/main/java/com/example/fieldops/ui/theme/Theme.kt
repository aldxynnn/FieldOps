package com.example.fieldops.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FieldOpsBlue = Color(0xFF1769FF)
private val FieldOpsBlueDark = Color(0xFF123B7A)
private val FieldOpsBlueLight = Color(0xFFEAF2FF)

private val FieldOpsBackground = Color(0xFFF5F9FE)
private val FieldOpsSurface = Color(0xFFFFFFFF)

private val FieldOpsText = Color(0xFF10213F)
private val FieldOpsTextSecondary = Color(0xFF667892)

private val FieldOpsGreen = Color(0xFF19B979)
private val FieldOpsGreenLight = Color(0xFFE8F8F1)

private val FieldOpsOrange = Color(0xFFFFA928)
private val FieldOpsOrangeLight = Color(0xFFFFF3DE)

private val FieldOpsRed = Color(0xFFE94B5F)
private val FieldOpsRedLight = Color(0xFFFFECEF)

private val LightColorScheme = lightColorScheme(

    primary = FieldOpsBlue,
    onPrimary = Color.White,

    primaryContainer = FieldOpsBlueLight,
    onPrimaryContainer = FieldOpsBlueDark,

    secondary = FieldOpsBlueDark,
    onSecondary = Color.White,

    tertiary = FieldOpsGreen,
    onTertiary = Color.White,

    background = FieldOpsBackground,
    onBackground = FieldOpsText,

    surface = FieldOpsSurface,
    onSurface = FieldOpsText,

    surfaceVariant = Color(0xFFF0F4FA),
    onSurfaceVariant = FieldOpsTextSecondary,

    error = FieldOpsRed,
    onError = Color.White,

    errorContainer = FieldOpsRedLight,
    onErrorContainer = Color(0xFF8B172A)
)

private val DarkColorScheme = darkColorScheme(

    primary = Color(0xFF79A9FF),
    onPrimary = Color(0xFF002E6D),

    primaryContainer = Color(0xFF0D47B5),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFFAFC9FF),
    onSecondary = Color(0xFF082D61),

    tertiary = Color(0xFF6DE0AE),
    onTertiary = Color(0xFF003823),

    background = Color(0xFF0B1422),
    onBackground = Color(0xFFE8EEF8),

    surface = Color(0xFF121D2C),
    onSurface = Color(0xFFE8EEF8),

    surfaceVariant = Color(0xFF25354B),
    onSurfaceVariant = Color(0xFFB9C6D9),

    error = Color(0xFFFF8A9A),
    onError = Color(0xFF5C0013)
)

@Composable
fun FieldOpsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = FieldOpsTypography,
        content = content
    )
}