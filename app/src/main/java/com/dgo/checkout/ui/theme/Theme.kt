package com.dgo.checkout.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Scheme = darkColorScheme(
    primary = BrandPurple,
    onPrimary = White,
    secondary = BrandPink,
    background = Black,
    onBackground = White,
    surface = Ink,
    onSurface = White,
    surfaceVariant = Color(0x14FFFFFF),
    outline = Color(0x1AFFFFFF),
    error = Danger,
)

@Composable
fun DgoCheckoutTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Scheme,
        typography = DgoTypography,
        content = content,
    )
}
