# Claude Instructions for NeonFlips

This file contains guidelines and context for AI coding assistants (like Claude) working on the NeonFlips Android project. If you are an AI assistant, read this file to understand the architecture, style, and rules of this codebase before making changes.

## 🏗️ Project Architecture

NeonFlips uses an **MVVM (Model-View-ViewModel)** architecture built entirely with **Jetpack Compose**. 

*   `domain/model`: Contains pure Kotlin data classes for state (e.g., `PlayerState`, `Obstacle`, `GameEngineState`).
*   `presentation/game`: Contains the core game logic, UI, and ViewModels.
    *   `GameScreen.kt`: The main Compose file handling overlays, score, menu UI, and instantiating the Canvas.
    *   `GameCanvas.kt`: A low-level Jetpack Compose `Canvas` block used to draw the entire game frame-by-frame (grid, geometric shapes, lighting glows, etc.).
    *   `GameEngine.kt`: The centralized physics engine. It recalculates the positions of obstacles and the player in a standalone Kotlin loop without tying itself directly to UI concepts.
    *   `GameViewModel.kt`: The bridge between the Compose state and the local DataStore (Scores, Settings).
*   `di`: Dependency Injection setup using **Hilt**.
*   `ui.theme`: Global color configurations—mostly filled with custom neon/cyan/pink palettes.

## 🎨 Visual Aesthetics & Rendering (GameCanvas)
The visual identity of this game relies heavily on **Neon Lighting** and **High Frame-Rate Animation**:
1.  **Avoid standard Compose Composables for the game view:** The actual gameplay (background, ball, walls) MUST be rendered using the raw Compose `Canvas` API (`drawRect`, `drawCircle`, `drawOval`, `drawLine`) to maintain 60FPS.
2.  **True Neon Glow Strategy:** To achieve a realistic neon glow, do not just make shapes solid colors. Draw a transparent, larger outer glow (`alpha = 0.3f`), then a solid mid-core of the color, and finally a smaller inner `Color.White` core.
3.  **Motion Blur:** The player character employs a squash-and-stretch mechanism. Modifying the player's shape must account for `velocityY` to stretch the circle into an oval.

## 🔊 Audio System
*   **Do NOT use `ToneGenerator`:** We have upgraded the audio. 
*   **Do use `SoundPool`:** Audio is handled through `SoundManager.kt` via the Android `SoundPool` API to prevent delay/lag. 
*   **Adding Sounds:** New `.wav` audio files should be generated completely synthetically (via Python scripts) and placed in `res/raw`.

## 🛠️ State Management & Physics
*   The overall game state flows uni-directionally: `GameEngine` runs a coroutine loop -> continuously updates `GameEngineState` -> `GameScreen` collects state -> `GameCanvas` renders state.
*   **Physics Loop:** `updatePlayerPhysics` and `updateObstacles` calculate frames deterministically. Gravity flips act as immediate vertical impulses.
*   **Difficulty Scaling:** The difficulty is procedurally generated. Gap height is inversely proportional to the current `score`, clamping at `minGap`. Do not hardcode fixed obstacle paths.

## 📝 Coding Conventions
*   **Language:** Kotlin 1.9+.
*   **Coroutines:** Use Coroutines/Flow for all asynchronous tasks and loops. Do not use RxJava or traditional Android threads.
*   **Dependency Injection:** Ensure all ViewModels are annotated with `@HiltViewModel` and all dependencies originate from the `AppModule.kt`. 
*   **Paths:** The local project root is `/Users/shubhamkumar/AndroidStudioProjects/NeonFlips`.

Follow these rules rigorously when proposing additions to ensure the game remains modular, performant, and visually stunning.
