package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GrjBlue80,
    onPrimary = GrjBlue10,
    primaryContainer = GrjBlue20,
    onPrimaryContainer = GrjBlue90,
    secondary = GrjGold80,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5A4005),
    onSecondaryContainer = GrjGold90,
    background = GrjDarkSurface,
    onBackground = GrjIvory,
    surface = GrjDarkCard,
    onSurface = GrjIvory,
    surfaceVariant = Color(0xFF1E2D44),
    onSurfaceVariant = Color(0xFFCBD5E1),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GrjBlue40,
    onPrimary = Color.White,
    primaryContainer = GrjBlue90,
    onPrimaryContainer = GrjBlue10,
    secondary = GrjGold50,
    onSecondary = Color.Black,
    secondaryContainer = GrjGold90,
    onSecondaryContainer = Color(0xFF4A3200),
    background = GrjIvory,
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = GrjCream,
    onSurfaceVariant = Color(0xFF475569),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve GRJ branding colors for consistency
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
