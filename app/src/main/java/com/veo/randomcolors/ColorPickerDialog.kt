package com.veo.randomcolors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.veo.randomcolors.ui.theme.RandomColorsTheme

val PRESET_COLORS = listOf(
    // Reds & Pinks
    Color(0xFFFF1744), Color(0xFFF44336), Color(0xFFE91E63), Color(0xFFFF4081),
    // Purples & Indigos
    Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF7C4DFF), Color(0xFF3F51B5),
    // Blues & Cyans
    Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4), Color(0xFF00E5FF),
    // Teals & Greens
    Color(0xFF009688), Color(0xFF1DE9B6), Color(0xFF4CAF50), Color(0xFF76FF03),
    // Limes & Yellows
    Color(0xFF8BC34A), Color(0xFFCDDC39), Color(0xFFFFEB3B), Color(0xFFFFD600),
    // Ambers & Oranges
    Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFFFF6E40),
    // Browns, Greys & Neutral Tones
    Color(0xFF795548), Color(0xFF8D6E63), Color(0xFF607D8B), Color(0xFF37474F),
    Color(0xFF000000), Color(0xFF757575), Color(0xFFBDBDBD), Color(0xFFFFFFFF),
)

@Composable
fun ColorPickerDialog(
    onDismissRequest: () -> Unit,
    onColorSelected: (Color) -> Unit,
    initialColor: Color = PRESET_COLORS.first(),
) {
    // Extract initial HSV
    val initialHsv = remember(initialColor) {
        colorToHsv(initialColor)
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    val currentColor = remember(hue, saturation, value) {
        hsvToColor(hue, saturation, value)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Add a Color",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Live preview banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = currentColor,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentColor.toHex(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = currentColor.contrastingTextColor()
                        )
                    }
                }

                // 2D Color Spectrum Box
                Text(
                    text = "Tap or Drag on Spectrum",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ColorSpectrumBox(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onColorChanged = { newHue, newSat ->
                        hue = newHue
                        saturation = newSat
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                // Brightness slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Brightness", style = MaterialTheme.typography.bodySmall)
                        Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                    }
                    Slider(
                        value = value,
                        onValueChange = { value = it },
                        valueRange = 0f..1f
                    )
                }

                Button(
                    onClick = {
                        val hsv = colorToHsv(getRandomRgbColor())
                        hue = hsv[0]
                        saturation = hsv[1]
                        value = hsv[2]
                    },
                ) {
                    Text("Random color")
                }

                // Preset Swatches Grid
                Text(
                    text = "Preset Swatches",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PRESET_COLORS.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(preset)
                                .border(
                                    width = if (preset.toHex() == currentColor.toHex()) 2.5.dp else 0.5.dp,
                                    color = if (preset.toHex() == currentColor.toHex()) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Gray.copy(alpha = 0.5f)
                                    },
                                    shape = CircleShape
                                )
                                .clickable {
                                    val hsv = colorToHsv(preset)
                                    hue = hsv[0]
                                    saturation = hsv[1]
                                    value = hsv[2]
                                }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onColorSelected(currentColor) }
            ) {
                Text("Add to Set")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ColorSpectrumBox(
    hue: Float,
    saturation: Float,
    value: Float,
    onColorChanged: (hue: Float, saturation: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val rainbowColors = remember {
        listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val h = (offset.x / size.width).coerceIn(0f, 1f) * 360f
                    val s = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                    onColorChanged(h, s)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val h = (change.position.x / size.width).coerceIn(0f, 1f) * 360f
                    val s = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                    onColorChanged(h, s)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Horizontal rainbow gradient across spectrum (Hue 0°..360°)
            drawRect(
                brush = Brush.horizontalGradient(colors = rainbowColors)
            )

            // Vertical gradient for saturation (Full color at top, desaturated white at bottom)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0f),
                        Color.White.copy(alpha = 0.9f)
                    )
                )
            )

            // Apply darkness overlay when value / brightness is decreased
            if (value < 1f) {
                drawRect(
                    color = Color.Black.copy(alpha = 1f - value)
                )
            }

            // Selector circle indicator at position corresponding to (hue, saturation)
            val posX = (hue / 360f) * width
            val posY = (1f - saturation) * height

            drawCircle(
                color = Color.White,
                radius = 10.dp.toPx(),
                center = Offset(posX, posY),
                style = Stroke(width = 3.dp.toPx())
            )
            drawCircle(
                color = Color.Black,
                radius = 11.5.dp.toPx(),
                center = Offset(posX, posY),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPickerDialogPreview() {
    RandomColorsTheme {
        ColorPickerDialog(
            onDismissRequest = {},
            onColorSelected = {}
        )
    }
}
