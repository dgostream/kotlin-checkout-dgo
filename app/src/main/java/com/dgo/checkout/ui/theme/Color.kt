package com.dgo.checkout.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val Ink = Color(0xFF08050F)
val Surface = Color(0xFF09060F)
val White = Color(0xFFFFFFFF)
val BrandPurple = Color(0xFF8A3FFC)
val BrandPink = Color(0xFFFF00BD)
val BrandOrange = Color(0xFFFF4D00)
val StripePurple = Color(0xFF635BFF)
val DevLime = Color(0xFFA3E635)
val Emerald = Color(0xFF6EE7B7)
val Danger = Color(0xFFFCA5A5)

val BrandGradient = Brush.horizontalGradient(
    listOf(BrandPurple, BrandPink, BrandOrange),
)

val ConfirmRingGradient = Brush.linearGradient(
    listOf(BrandPurple, BrandPink),
)
