package com.veo.randomcolors

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ColorPickerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ColorPreferencesRepository(application)

    val isRgbSpectrumMode: StateFlow<Boolean> = repository.isRgbSpectrumMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val colorSet: StateFlow<List<Color>> = repository.colorSet
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DEFAULT_COLORS
        )

    val currentColor: StateFlow<Color?> = repository.currentColor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DEFAULT_COLORS.firstOrNull()
        )

    fun toggleRgbSpectrumMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setRgbSpectrumMode(enabled)
            if (enabled && (currentColor.value == null)) {
                pickRandomColor()
            }
        }
    }

    fun addColor(color: Color) {
        viewModelScope.launch {
            val currentList = colorSet.value.toMutableList()
            if (currentList.none { it.toHex() == color.toHex() }) {
                currentList.add(color)
                repository.saveColorSet(currentList)
            }
            repository.saveCurrentColor(color)
        }
    }

    fun removeColor(color: Color) {
        viewModelScope.launch {
            val currentList = colorSet.value.filterNot { it.toHex() == color.toHex() }
            repository.saveColorSet(currentList)
        }
    }

    fun clearColors() {
        viewModelScope.launch {
            repository.saveColorSet(emptyList())
        }
    }

    fun resetColors() {
        viewModelScope.launch {
            repository.saveColorSet(DEFAULT_COLORS)
            if (currentColor.value == null) {
                repository.saveCurrentColor(DEFAULT_COLORS.random())
            }
        }
    }

    fun selectColor(color: Color) {
        viewModelScope.launch {
            repository.saveCurrentColor(color)
        }
    }

    fun pickRandomColor() {
        val currentIsRgb = isRgbSpectrumMode.value
        val currentSet = colorSet.value

        val newColor = if (currentIsRgb) {
            getRandomRgbColor()
        } else if (currentSet.isEmpty()) {
            null
        } else {
            currentSet.random()
        }

        viewModelScope.launch {
            repository.saveCurrentColor(newColor)
        }
    }
}
