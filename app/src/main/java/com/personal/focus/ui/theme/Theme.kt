package com.personal.focus.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Neutral0,
    primaryContainer = SplashBg,
    onPrimaryContainer = Neutral100,
    secondary = Gold,
    onSecondary = Neutral0,
    tertiary = SecondaryOrange,
    onTertiary = Neutral0,
    background = Neutral0,
    onBackground = Neutral100,
    surface = DarkSurface,
    onSurface = Neutral100,
    surfaceVariant = Neutral10,
    onSurfaceVariant = Neutral90,
    error = ErrorToastBackground,
    onError = Neutral100
)

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun FocusTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            try {
                val window = view.context.findActivity()?.window
                if (window != null) {
                    window.navigationBarColor = NavBarColor.toArgb()
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    insetsController.isAppearanceLightStatusBars = false
                    insetsController.isAppearanceLightNavigationBars = false
                }
            } catch (_: Throwable) {
                // Ignore window decoration errors on translucent overlay windows
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FocusTypography,
        content = content
    )
}
