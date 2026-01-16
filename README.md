# InfiXOX

A modern Android Tic-Tac-Toe game with a twist - **Infinite Mode**! Each player can only have 3 pieces on the board at a time. When you place a 4th piece, your oldest piece disappears.

## Features

### Core Gameplay
- **Infinite Mode**: Maximum 3 pieces per player on the board
- **Auto-remove oldest piece**: When placing a 4th piece, the oldest one vanishes
- **Best of 5 rounds**: First to win 3 rounds wins the match
- **Alternating starts**: Players alternate who goes first each round

### AI Opponent
- **7-tier decision tree AI** with strategic priorities:
  1. Win immediately if possible
  2. Block opponent's winning move
  3. Create fork opportunities (two ways to win)
  4. Block opponent's forks
  5. Take center position
  6. Take corner positions
  7. Take any available edge

### UI/UX
- **Dark theme** with modern color scheme
- **Animated splash screen** with logo fade-in and scale animation
- **Confetti celebration** when match is won
  - Gold/Green confetti for player victory
  - Red confetti for AI victory
- **Visual feedback** for winning combinations
- **Piece counter** showing pieces on board for each player
- **Score tracking** across rounds

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM with StateFlow
- **Minimum SDK**: 28 (Android 9.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/src/main/java/com/example/infinite_xox/
├── MainActivity.kt
├── model/
│   └── GameModels.kt          # Data classes & constants
├── game/
│   ├── GameLogic.kt           # Win detection, draw logic
│   └── AIStrategy.kt          # 7-tier AI decision tree
├── viewmodel/
│   └── GameViewModel.kt       # Game state management
└── ui/
    ├── theme/                 # Colors, typography, theme
    ├── screens/
    │   ├── SplashScreen.kt    # Animated splash
    │   └── GameScreen.kt      # Main game screen
    └── components/
        ├── GameBoard.kt       # 3x3 grid
        ├── ScoreBoard.kt      # Player vs AI scores
        ├── GameStatus.kt      # Turn/status messages
        ├── GameControls.kt    # Reset/New game buttons
        ├── PiecesCounter.kt   # Piece count display
        ├── InfoDialog.kt      # How to play
        ├── GameOverDialog.kt  # Match result
        └── Confetti.kt        # Victory animation
```

## Development Progress

### Completed
- [x] Initial project setup with Jetpack Compose
- [x] Game state data models
- [x] Win detection and game logic
- [x] Infinite mode (max 3 pieces, auto-remove oldest)
- [x] AI strategy with 7-tier decision tree
- [x] GameViewModel with coroutine-based state management
- [x] Dark theme UI
- [x] Game board with cell animations
- [x] Score tracking (best of 5)
- [x] Info dialog (how to play)
- [x] Game over dialog
- [x] Confetti animation for match winner
- [x] Splash screen with animated logo
- [x] App icon and branding (InfiXOX)
- [x] Unit tests for game logic and AI

### Version History

| Version | Changes |
|---------|---------|
| 1.0.0   | Initial release with full gameplay, AI opponent, splash screen, and confetti animations |

## Building

```bash
# Debug build
./gradlew assembleDebug

# Run tests
./gradlew test

# Install on device
./gradlew installDebug
```

## Screenshots

*Coming soon*

## License

MIT License

---

Made with Jetpack Compose
