# NeonFlips

**NeonFlips** is a fast-paced, glowing neon arcade challenge for Android, built with Jetpack Compose. Navigate a hyper-stylized glowing orb through an endless series of neon obstacles by tapping the screen to flip gravity!

## 🚀 Features

*   **Gravity-Flipping Mechanics:** A simple, one-tap control system that reverses gravity instantly.
*   **Dynamic Difficulty Curve:** The game starts easy with wide gaps and progressively gets harder as your score increases!
*   **Stunning Neon Visuals:** Features a dark-cyan panning neon grid background, multi-layered glowing geometric obstacles, and squash-and-stretch motion blur on the player character.
*   **Retro Synth Audio:** Custom-synthesized 8-bit `.wav` audio powered by Android's `SoundPool` for responsive, instantaneous hit feedback.
*   **Persistent Scores:** Uses Jetpack DataStore to persistently save high scores and settings between sessions.
*   **Google Play Store Ready:** Contains the signed bundle configuration and legal web pages (Privacy Policy, Terms of Service) required for publishing.

## 🛠️ Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose & Custom Canvas Rendering
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Dependency Injection:** Hilt
*   **Local Storage:** Jetpack DataStore (Preferences)
*   **Audio:** Android `SoundPool` API
*   **Build System:** Gradle (Kotlin DSL)

## 📋 Prerequisites

To build and run this project, you need:

1.  [Android Studio](https://developer.android.com/studio) (Koala or newer Recommended).
2.  Java Development Kit (JDK) 17 or higher.
3.  An Android device or emulator running API level 24 (Android 7.0) or higher.

## ⚙️ Building and Running

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/shubham21155102/Neon-Flip-Android.git
    cd Neon-Flip-Android
    ```
2.  **Open in Android Studio:**
    Open Android Studio -> Open -> Select the `Neon-Flip-Android` directory.
3.  **Sync Gradle:**
    Allow Android Studio to download dependencies and sync the project files.
4.  **Run:**
    Connect a physical device via ADB or start an emulator. Click the ▶️ **Run 'app'** button in the top toolbar.

## 🎮 How to Play

1.  Tap **PLAY** on the main menu.
2.  Your glowing orb automatically moves forward.
3.  **Tap anywhere on the screen** to instantly flip gravity (up or down).
4.  Guide the ball through the gaps in the illuminated walls.
5.  If you hit a wall or the floor/ceiling, it's Game Over!

## 📜 Publishing to Play Store

Check the included `play_store_guide.md` in the root directory for a comprehensive step-by-step guide on generating a signed `.aab` bundle and filling out metadata on the Google Play Developer Console.

Included in the `web/` directory are the required Privacy Policy and Terms of Conditions standard HTML pages ready to be deployed to GitHub Pages or Firebase.

## 👨‍💻 Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change. 

If making changes to Physics or UI Canvas, see `CLAUDE.md` for specific architectural conventions used in this project.
