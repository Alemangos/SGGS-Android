package com.unellez.sggs.ui.theme

import androidx.compose.ui.graphics.Color
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = UnellezLightBlue,
    secondary = UnellezGold,
    tertiary = UnellezBlue,
    background = DarkSurface,
    surface = DarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = UnellezBlue,
    secondary = UnellezGold,
    tertiary = UnellezLightBlue,
    background = BackgroundLight,
    surface = Color.White

    /* Si quieres afinar más colores, puedes agregarlos aquí:
    onPrimary = Color.White,
    onSecondary = Color.Black,
    */
)

@Composable
fun SGGSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}