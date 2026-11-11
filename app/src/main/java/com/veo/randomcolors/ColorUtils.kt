package com.veo.randomcolors

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Returns a hex string representation of the color in the format "#RRGGBB".
 */
fun Color.toHex(): String {
    val red = (this.red * 255).toInt().coerceIn(0, 255)
    val green = (this.green * 255).toInt().coerceIn(0, 255)
    val blue = (this.blue * 255).toInt().coerceIn(0, 255)
    return String.format("#%02X%02X%02X", red, green, blue)
}

/**
 * Determines a contrasting content color (black or white) based on the background luminance.
 */
fun Color.contrastingTextColor(): Color {
    return if (this.luminance() > 0.5f) Color.Black else Color.White
}

/**
 * Converts HSV values to a Compose [Color].
 * @param hue Hue angle in degrees [0f, 360f]
 * @param saturation Saturation [0f, 1f]
 * @param value Value / Brightness [0f, 1f]
 */
fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
    val hsv = floatArrayOf(
        hue.coerceIn(0f, 360f),
        saturation.coerceIn(0f, 1f),
        value.coerceIn(0f, 1f),
    )
    val argb = AndroidColor.HSVToColor(hsv)
    return Color(argb)
}

fun colorToHsv(color: Color): FloatArray {
    val hsv = FloatArray(3)
    val red = (color.red * 255).toInt()
    val green = (color.green * 255).toInt()
    val blue = (color.blue * 255).toInt()
    AndroidColor.RGBToHSV(red, green, blue, hsv)
    return hsv
}

/**
 * Converts a hex string in the format "#RRGGBB" or "#AARRGGBB" to a Compose [Color].
 */
fun hexToColor(hex: String): Color? {
    return try {
        val colorInt = AndroidColor.parseColor(hex.trim())
        Color(colorInt)
    } catch (_: Exception) {
        null
    }
}

/**
 * Generates a random [Color] across the full RGB spectrum.
 */
fun getRandomRgbColor(): Color {
    return Color(
        red = kotlin.random.Random.nextFloat(),
        green = kotlin.random.Random.nextFloat(),
        blue = kotlin.random.Random.nextFloat(),
        alpha = 1.0f,
    )
}
