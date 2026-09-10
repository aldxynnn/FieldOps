package com.example.fieldops.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = FieldOpsPrimary,
    onPrimary = Color.White,
    primaryContainer = FieldOpsPrimarySoft,
    onPrimaryContainer = FieldOpsPrimaryDark,
    secondary = FieldOpsPrimaryDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFF4FF),
    onSecondaryContainer = FieldOpsInk,
    tertiary = FieldOpsSuccess,
    onTertiary = Color.White,
    tertiaryContainer = FieldOpsSuccessSoft,
    onTertiaryContainer = Color(0xFF075B3A),
    background = FieldOpsBackground,
    onBackground = FieldOpsInk,
    surface = FieldOpsSurface,
    onSurface = FieldOpsInk,
    surfaceVariant = Color(0xFFF0F3F8),
    onSurfaceVariant = FieldOpsMuted,
    outline = FieldOpsBorder,
    outlineVariant = Color(0xFFE9EDF4),
    error = FieldOpsDanger,
    onError = Color.White,
    errorContainer = FieldOpsDangerSoft,
    onErrorContainer = Color(0xFF8B172A)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8CB4FF),
    onPrimary = Color(0xFF052C70),
    primaryContainer = Color(0xFF174A9F),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFB9CCF4),
    onSecondary = Color(0xFF122747),
    tertiary = Color(0xFF72D9AC),
    onTertiary = Color(0xFF003823),
    background = Color(0xFF09111F),
    onBackground = Color(0xFFEAF0FA),
    surface = Color(0xFF111B2B),
    onSurface = Color(0xFFEAF0FA),
    surfaceVariant = Color(0xFF1D2A3D),
    onSurfaceVariant = Color(0xFFB5C2D5),
    outline = Color(0xFF3A4A62),
    outlineVariant = Color(0xFF26354A),
    error = Color(0xFFFF8D9C),
    onError = Color(0xFF5C0013),
    errorContainer = Color(0xFF7A172A),
    onErrorContainer = Color.White
)

private val FieldOpsShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
)

@Composable
fun FieldOpsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = FieldOpsTypography,
        shapes = FieldOpsShapes,
        content = content
    )
}
