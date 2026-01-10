# Android Music App 🎵

A premium, offline music player for Android that combines the feature set of Samsung Music with the aesthetic design of Apple Music.

## Features ✨

*   **Offline Music Library**: Automatically scans your device for audio files. No internet connection required.
*   **Premium Apple-Style UI**: Clean typography, blur effects, and smooth animations built with Jetpack Compose.
*   **Background Playback**: Continues playing music when the app is closed or the screen is off (uses `androidx.media3`).
*   **Smart Queue**: Automatically plays the next song in the list.
*   **Functional Library**: Organize your music by **Tracks**, **Artists**, and **Albums**.
*   **Real-time Controls**: 
    *   Interactive seek bar with time labels.
    *   Mini-player for quick access.
    *   Notification controls.

## Prerequisites 🛠️

To build and run this project, you need:

*   **Android Studio** (Hedgehog or newer recommended).
*   **JDK 17** (usually comes bundled with Android Studio).
*   An Android Device or Emulator running **Android 8.0 (Oreo)** or higher.

## How to Build & Run 🚀

1.  **Clone/Open the Project**:
    *   Open Android Studio.
    *   Select **Open** and navigate to the `mobile music app` folder.

2.  **Sync Gradle**:
    *   Android Studio should automatically detect the `build.gradle.kts` files.
    *   If asked, click **Sync Now** to download dependencies (Compose, Media3, Coil).

3.  **Run the App**:
    *   Connect your Android device via USB (ensure USB Debugging is on) or create an Emulator.
    *   Click the green **Run** (▶️) button in the toolbar.

## Build Commands 📦

To build the APK from the command line:

```bash
# Debug APK (for development/testing)
 ./gradlew.bat assembleDebug

# Release APK (for production)
./gradlew.bat assembleRelease

# Build and install directly to connected device
./gradlew.bat installDebug
```

**APK Output Locations:**
*   Debug: `app/build/outputs/apk/debug/app-debug.apk`
*   Release: `app/build/outputs/apk/release/app-release.apk`

## Permissions 🔒

On the first launch, the app will request the following permissions to function:

*   **Storage / Music & Audio**: To find your songs.
*   **Notifications**: To show the player controls in the notification shade.

If you denied permissions by mistake, click the **"Grant Access"** button on the main screen.

---
*Built with Kotlin, Jetpack Compose, and Media3.*
