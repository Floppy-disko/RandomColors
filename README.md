# RandomColors

RandomColors is an Android application built with **Jetpack Compose**. It displays colors chosen at random form the **rgb** spectrum or from a set created by the user.

---

## Features

- **Dual Color Generation Modes**:
  - **RGB Spectrum Mode**: Generate random colors across the 24-bit RGB spectrum.
  - **Custom Color Set Mode**: Randomly select from a user-curated palette of saved colors.
- **Custom Palette Management**:
  - Add new colors to the set using a color picker dialog.
  - Quick-select colors directly from palette chips.
  - Delete individual chips, clear the palette, or reset to a default preset of colors.
- **⛶ Full-Screen View**: Expand the box displaying the random color to full screen.
- **💾 Persistent Data**: Saves set of selected chosen colors across app restarts.
- **Dark mode**: Light/dark mode depending on system settings, using Material 3.

---

## Development

I developed this app in Android Studio, and I helped myself with **AI**, in the form of Gemini models.

---

## 📁 Project Structure

```
RandomColors/
├── app/
│   ├── src/main/java/com/veo/randomcolors/
│   │   ├── MainActivity.kt                  # Main entry point & Edge-to-Edge setup
│   │   ├── ColorPickerScreen.kt             # Main Compose UI layout & interactions
│   │   ├── ColorPickerViewModel.kt          # ViewModel managing UI state & DataStore
│   │   ├── ColorPreferencesRepository.kt    # DataStore persistence layer
│   │   ├── ColorPickerDialog.kt             # Dialog for adding custom colors
│   │   └── ColorUtils.kt                    # Hex conversion & contrast math utilities
│   └── src/main/res/                        # Resources & XML launcher icons
├── gradle/
│   └── libs.versions.toml                   # Gradle Version Catalog
├── build.gradle.kts                         # Top-level build script
└── settings.gradle.kts                      # Repository settings
```

---

