package com.veo.randomcolors

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun colorToHex_isCorrect() {
        val red = Color(0xFFFF0000)
        assertEquals("#FF0000", red.toHex())

        val green = Color(0xFF00FF00)
        assertEquals("#00FF00", green.toHex())

        val custom = Color(0xFFE91E63)
        assertEquals("#E91E63", custom.toHex())
    }

    @Test
    fun contrastingTextColor_isCorrect() {
        // Pure white background should have black text
        assertEquals(Color.Black, Color.White.contrastingTextColor())
        // Pure black background should have white text
        assertEquals(Color.White, Color.Black.contrastingTextColor())
    }
}
