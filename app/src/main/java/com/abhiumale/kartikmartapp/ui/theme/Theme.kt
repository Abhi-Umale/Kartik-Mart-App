package com.abhiumale.kartikmartapp.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLightGreen,          //Button colors
    onPrimary = Color.Black,              //Main button ke andar ka content
    secondary = PrimaryGreen,             //filter chips/progress bar/checksBox
    onSecondary = Color.White,            //Discount
    background = DarkBackground,          //background
    surface = DarkSurface,                //card box
    onBackground = DarkTextPrimary,       //headlineTitle
    onSurface = DarkTextPrimary,          //Normal body text
    outline = PrimaryDarkGreen,           //Shadow/Border
    error = ErrorRed,
    onSurfaceVariant = White,
    tertiary = Gray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,                 //Button colors
    onPrimary = Color.White,                //Main button ke andar ka content
    secondary = PrimaryDarkGreen,           //filter chips/progress bar/checksBox
    onSecondary = Color.White,              //Discount
    background = LightBackground,           //background
    surface = LightSurface,                 //card box
    onBackground = LightTextPrimary,        //headline,Title
    onSurface = LightTextSecondary,         //Normal body text
    outline = PrimaryGreen,                 //Shadow/Border
    error = ErrorRed,
    onSurfaceVariant = Black,
    tertiary = DarkGray
)

@Composable
fun KartikMartAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color features (Android 12+) ko false rakha hai
    // taaki aapka Green theme hi dikhe, system ka wallpaper color nahi.
    dynamicColor: Boolean = false,
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