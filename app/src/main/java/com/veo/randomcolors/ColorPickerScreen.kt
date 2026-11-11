package com.veo.randomcolors

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.veo.randomcolors.ui.theme.RandomColorsTheme

val DEFAULT_COLORS = listOf(
    Color(0xFFE91E63), // Pink
    Color(0xFF9C27B0), // Purple
    Color(0xFF2196F3), // Blue
    Color(0xFF4CAF50), // Green
    Color(0xFFFFC107), // Amber
    Color(0xFFFF5722), // Deep Orange
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerScreen(
    modifier: Modifier = Modifier,
    viewModel: ColorPickerViewModel = viewModel()
) {
    // Collect persistent state from DataStore via ViewModel
    val isRgbSpectrumMode by viewModel.isRgbSpectrumMode.collectAsState()
    val colorSet by viewModel.colorSet.collectAsState()
    val savedCurrentColor by viewModel.currentColor.collectAsState()

    val currentColor = savedCurrentColor ?: colorSet.firstOrNull()

    // Transient UI state preserved across configuration changes (e.g. rotation) using rememberSaveable
    var showAddColorDialog by rememberSaveable { mutableStateOf(false) }
    var isFullScreen by rememberSaveable { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        targetValue = currentColor ?: MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 350),
        label = "HeroColorAnimation"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Random Color Picker",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { viewModel.pickRandomColor() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = animatedColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentColor != null) {
                        val textColor = animatedColor.contrastingTextColor()
                        IconButton(
                            onClick = { isFullScreen = true },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = "⛶",
                                style = MaterialTheme.typography.titleLarge,
                                color = textColor
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = animatedColor.toHex(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor
                            )
                            Text(
                                text = "Tap card to randomize",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor.copy(alpha = 0.8f)
                            )
                        }
                    } else {
                        Text(
                            text = if (isRgbSpectrumMode) {
                                "Tap card or button to pick a random RGB spectrum color."
                            } else {
                                "No colors in your set!\nAdd some colors below or enable RGB spectrum mode."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Option Slider Card: Randomize from RGB Spectrum vs Color Set
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "RGB Spectrum Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isRgbSpectrumMode) {
                                "Randomizing from full 24-bit RGB spectrum"
                            } else {
                                "Randomizing from saved Color Set"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isRgbSpectrumMode,
                        onCheckedChange = { enabled ->
                            viewModel.toggleRgbSpectrumMode(enabled)
                        }
                    )
                }
            }

            // Pick Random Button
            Button(
                onClick = { viewModel.pickRandomColor() },
                enabled = isRgbSpectrumMode || colorSet.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "🎲  Pick Random Color",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Palette Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Color Set",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${colorSet.size} color${if (colorSet.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (colorSet.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearColors() }
                        ) {
                            Text("Clear")
                        }
                    }
                    if (colorSet.isEmpty()) {
                        TextButton(
                            onClick = { viewModel.resetColors() }
                        ) {
                            Text("Reset")
                        }
                    }
                    FilledTonalButton(
                        onClick = { showAddColorDialog = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add")
                    }
                }
            }

            // Palette Chips FlowRow
            if (colorSet.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    colorSet.forEach { color ->
                        ColorChip(
                            color = color,
                            isSelected = currentColor?.toHex() == color.toHex(),
                            onSelect = { viewModel.selectColor(color) },
                            onDelete = { viewModel.removeColor(color) }
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add colors using the '+ Add' button above to build your random selection pool.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Color Picker Dialog
    if (showAddColorDialog) {
        ColorPickerDialog(
            onDismissRequest = { showAddColorDialog = false },
            onColorSelected = { newColor ->
                viewModel.addColor(newColor)
                showAddColorDialog = false
            }
        )
    }

    // Full Screen Color View Dialog
    if (isFullScreen) {
        Dialog(
            onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            val textColor = animatedColor.contrastingTextColor()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animatedColor)
                    .clickable { viewModel.pickRandomColor() },
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { isFullScreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                if (currentColor == null) {
                    Text(
                        text = "No colors in your set!",
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun ColorChip(
    color: Color,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        }
    ) {
        Row(
            modifier = Modifier.padding(start = 6.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Color dot
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(0.5.dp, Color.Gray.copy(alpha = 0.4f), CircleShape)
            )

            // Hex code
            Text(
                text = color.toHex(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPickerScreenPreview() {
    RandomColorsTheme {
        ColorPickerScreen()
    }
}
