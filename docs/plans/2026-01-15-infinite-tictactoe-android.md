# Infinite Tic-Tac-Toe Android Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Create an Android version of the Infinite Tic-Tac-Toe web game with identical logic and gameplay experience.

**Architecture:** Single-activity Android app with Jetpack Compose for UI. Game logic encapsulated in a ViewModel following MVVM pattern. AI strategy implemented as a separate utility class.

**Tech Stack:** Kotlin, Jetpack Compose, Material Design 3, ViewModel, StateFlow

**Source Reference:** `/Users/jeripurnamamaulid/Documents/14_Web-projects/jerrypm.github.io/script.js`

---

## Game Rules Summary

1. 3x3 grid Tic-Tac-Toe dengan mekanik "Infinite"
2. Setiap pemain maksimal 3 pieces di board
3. Piece ke-4 akan memindahkan piece tertua ke posisi baru
4. Menang 3 round untuk menang match
5. First move bergantian setiap round
6. AI dengan 5-tier decision tree

---

## Task 1: Setup Project Dependencies

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `gradle/libs.versions.toml`

**Step 1: Update version catalog untuk Compose dependencies**

Edit `gradle/libs.versions.toml`:
```toml
[versions]
agp = "8.7.2"
kotlin = "1.9.24"
coreKtx = "1.10.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
appcompat = "1.6.1"
material = "1.10.0"
composeBom = "2024.02.00"
lifecycleRuntime = "2.7.0"
activityCompose = "1.8.2"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-lifecycle-runtime = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntime" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntime" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

**Step 2: Update app/build.gradle.kts untuk Compose**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.infinite_xox"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.infinite_xox"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```

**Step 3: Sync Gradle dan verify build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add .
git commit -m "chore: setup Jetpack Compose dependencies"
```

---

## Task 2: Create Game State Data Models

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/model/GameModels.kt`
- Create: `app/src/test/java/com/example/infinite_xox/model/GameModelsTest.kt`

**Step 1: Write failing tests untuk data models**

```kotlin
package com.example.infinite_xox.model

import org.junit.Assert.*
import org.junit.Test

class GameModelsTest {

    @Test
    fun `CellState should have correct symbols`() {
        assertEquals("X", CellState.X.symbol)
        assertEquals("O", CellState.O.symbol)
        assertEquals("", CellState.EMPTY.symbol)
    }

    @Test
    fun `Move should store index symbol and timestamp`() {
        val move = Move(index = 4, symbol = CellState.X, timestamp = 1000L)
        assertEquals(4, move.index)
        assertEquals(CellState.X, move.symbol)
        assertEquals(1000L, move.timestamp)
    }

    @Test
    fun `GameState should have correct initial values`() {
        val state = GameState()
        assertEquals(9, state.board.size)
        assertTrue(state.board.all { it == CellState.EMPTY })
        assertTrue(state.moveHistory.isEmpty())
        assertTrue(state.isPlayerTurn)
        assertEquals(0, state.playerScore)
        assertEquals(0, state.aiScore)
        assertFalse(state.isGameOver)
        assertEquals(0, state.roundCount)
    }

    @Test
    fun `WINNING_COMBINATIONS should have 8 patterns`() {
        assertEquals(8, GameConstants.WINNING_COMBINATIONS.size)
    }
}
```

**Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "com.example.infinite_xox.model.GameModelsTest"`
Expected: FAIL - classes not found

**Step 3: Write data models implementation**

```kotlin
package com.example.infinite_xox.model

enum class CellState(val symbol: String) {
    X("X"),
    O("O"),
    EMPTY("")
}

data class Move(
    val index: Int,
    val symbol: CellState,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameState(
    val board: List<CellState> = List(9) { CellState.EMPTY },
    val moveHistory: List<Move> = emptyList(),
    val isPlayerTurn: Boolean = true,
    val playerScore: Int = 0,
    val aiScore: Int = 0,
    val isGameOver: Boolean = false,
    val isMatchOver: Boolean = false,
    val roundCount: Int = 0,
    val playerStartedLastRound: Boolean = true,
    val winningCombination: List<Int>? = null,
    val statusMessage: String = "Your Turn (X)",
    val isProcessingMove: Boolean = false
)

object GameConstants {
    const val MAX_PIECES = 3
    const val ROUNDS_TO_WIN = 3
    const val AI_MOVE_DELAY_MS = 300L

    val WINNING_COMBINATIONS = listOf(
        listOf(0, 1, 2), // Row 1
        listOf(3, 4, 5), // Row 2
        listOf(6, 7, 8), // Row 3
        listOf(0, 3, 6), // Column 1
        listOf(1, 4, 7), // Column 2
        listOf(2, 5, 8), // Column 3
        listOf(0, 4, 8), // Diagonal 1
        listOf(2, 4, 6)  // Diagonal 2
    )

    val CORNERS = listOf(0, 2, 6, 8)
    const val CENTER = 4
}
```

**Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "com.example.infinite_xox.model.GameModelsTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: add game state data models"
```

---

## Task 3: Implement Win Detection Logic

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/game/GameLogic.kt`
- Create: `app/src/test/java/com/example/infinite_xox/game/GameLogicTest.kt`

**Step 1: Write failing tests for win detection**

```kotlin
package com.example.infinite_xox.game

import com.example.infinite_xox.model.CellState
import org.junit.Assert.*
import org.junit.Test

class GameLogicTest {

    private val gameLogic = GameLogic()

    @Test
    fun `checkWinner returns winning combination for row win`() {
        val board = listOf(
            CellState.X, CellState.X, CellState.X,
            CellState.O, CellState.O, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.EMPTY
        )
        val result = gameLogic.checkWinner(board, CellState.X)
        assertEquals(listOf(0, 1, 2), result)
    }

    @Test
    fun `checkWinner returns winning combination for column win`() {
        val board = listOf(
            CellState.O, CellState.X, CellState.EMPTY,
            CellState.O, CellState.X, CellState.EMPTY,
            CellState.O, CellState.EMPTY, CellState.EMPTY
        )
        val result = gameLogic.checkWinner(board, CellState.O)
        assertEquals(listOf(0, 3, 6), result)
    }

    @Test
    fun `checkWinner returns winning combination for diagonal win`() {
        val board = listOf(
            CellState.X, CellState.O, CellState.EMPTY,
            CellState.O, CellState.X, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.X
        )
        val result = gameLogic.checkWinner(board, CellState.X)
        assertEquals(listOf(0, 4, 8), result)
    }

    @Test
    fun `checkWinner returns null when no winner`() {
        val board = listOf(
            CellState.X, CellState.O, CellState.X,
            CellState.O, CellState.X, CellState.O,
            CellState.O, CellState.X, CellState.O
        )
        val result = gameLogic.checkWinner(board, CellState.X)
        assertNull(result)
    }

    @Test
    fun `countSymbols returns correct count`() {
        val board = listOf(
            CellState.X, CellState.X, CellState.EMPTY,
            CellState.O, CellState.X, CellState.EMPTY,
            CellState.O, CellState.EMPTY, CellState.EMPTY
        )
        assertEquals(3, gameLogic.countSymbols(board, CellState.X))
        assertEquals(2, gameLogic.countSymbols(board, CellState.O))
    }

    @Test
    fun `getAvailableMoves returns empty cell indices`() {
        val board = listOf(
            CellState.X, CellState.X, CellState.EMPTY,
            CellState.O, CellState.EMPTY, CellState.EMPTY,
            CellState.O, CellState.EMPTY, CellState.EMPTY
        )
        assertEquals(listOf(2, 4, 5, 7, 8), gameLogic.getAvailableMoves(board))
    }
}
```

**Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "com.example.infinite_xox.game.GameLogicTest"`
Expected: FAIL

**Step 3: Implement GameLogic class**

```kotlin
package com.example.infinite_xox.game

import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameConstants
import com.example.infinite_xox.model.Move

class GameLogic {

    fun checkWinner(board: List<CellState>, symbol: CellState): List<Int>? {
        for (combo in GameConstants.WINNING_COMBINATIONS) {
            if (combo.all { board[it] == symbol }) {
                return combo
            }
        }
        return null
    }

    fun countSymbols(board: List<CellState>, symbol: CellState): Int {
        return board.count { it == symbol }
    }

    fun getAvailableMoves(board: List<CellState>): List<Int> {
        return board.indices.filter { board[it] == CellState.EMPTY }
    }

    fun getOldestMoveIndex(moveHistory: List<Move>, symbol: CellState): Int? {
        return moveHistory
            .filter { it.symbol == symbol }
            .minByOrNull { it.timestamp }
            ?.index
    }

    fun isDraw(board: List<CellState>, moveHistory: List<Move>): Boolean {
        val playerPieces = countSymbols(board, CellState.X)
        val aiPieces = countSymbols(board, CellState.O)
        val emptyCount = countSymbols(board, CellState.EMPTY)

        if (playerPieces < GameConstants.MAX_PIECES || aiPieces < GameConstants.MAX_PIECES) {
            return false
        }

        if (emptyCount != 3) return false

        return !canAnyoneWin(board)
    }

    private fun canAnyoneWin(board: List<CellState>): Boolean {
        val availableMoves = getAvailableMoves(board)

        for (symbol in listOf(CellState.X, CellState.O)) {
            for (move in availableMoves) {
                val testBoard = board.toMutableList()
                testBoard[move] = symbol
                if (checkWinner(testBoard, symbol) != null) {
                    return true
                }
            }
        }
        return false
    }
}
```

**Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "com.example.infinite_xox.game.GameLogicTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: implement win detection and game logic utilities"
```

---

## Task 4: Implement AI Strategy

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/game/AIStrategy.kt`
- Create: `app/src/test/java/com/example/infinite_xox/game/AIStrategyTest.kt`

**Step 1: Write failing tests for AI strategy**

```kotlin
package com.example.infinite_xox.game

import com.example.infinite_xox.model.CellState
import org.junit.Assert.*
import org.junit.Test

class AIStrategyTest {

    private val aiStrategy = AIStrategy()
    private val gameLogic = GameLogic()

    @Test
    fun `AI should win when possible`() {
        val board = listOf(
            CellState.O, CellState.O, CellState.EMPTY,
            CellState.X, CellState.X, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.EMPTY
        )
        val availableMoves = gameLogic.getAvailableMoves(board)
        val move = aiStrategy.findBestMove(board, availableMoves)
        assertEquals(2, move)
    }

    @Test
    fun `AI should block player win`() {
        val board = listOf(
            CellState.X, CellState.X, CellState.EMPTY,
            CellState.O, CellState.EMPTY, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.EMPTY
        )
        val availableMoves = gameLogic.getAvailableMoves(board)
        val move = aiStrategy.findBestMove(board, availableMoves)
        assertEquals(2, move)
    }

    @Test
    fun `AI should take center when available and no immediate threat`() {
        val board = listOf(
            CellState.X, CellState.EMPTY, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.EMPTY
        )
        val availableMoves = gameLogic.getAvailableMoves(board)
        val move = aiStrategy.findBestMove(board, availableMoves)
        assertEquals(4, move)
    }

    @Test
    fun `findWinningMove returns correct index`() {
        val board = listOf(
            CellState.O, CellState.O, CellState.EMPTY,
            CellState.X, CellState.X, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.EMPTY
        )
        val move = aiStrategy.findWinningMove(board, CellState.O)
        assertEquals(2, move)
    }

    @Test
    fun `findWinningMove returns null when no winning move`() {
        val board = listOf(
            CellState.O, CellState.EMPTY, CellState.EMPTY,
            CellState.X, CellState.X, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.EMPTY
        )
        val move = aiStrategy.findWinningMove(board, CellState.O)
        assertNull(move)
    }
}
```

**Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "com.example.infinite_xox.game.AIStrategyTest"`
Expected: FAIL

**Step 3: Implement AIStrategy class**

```kotlin
package com.example.infinite_xox.game

import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameConstants

class AIStrategy {

    private val gameLogic = GameLogic()

    fun findBestMove(board: List<CellState>, availableMoves: List<Int>): Int {
        if (availableMoves.isEmpty()) {
            throw IllegalStateException("No available moves")
        }

        // Priority 1: Win if possible
        findWinningMove(board, CellState.O)?.let { return it }

        // Priority 2: Block player from winning
        findWinningMove(board, CellState.X)?.let { return it }

        // Priority 3: Create a fork (2+ ways to win)
        findForkingMove(board, CellState.O, availableMoves)?.let { return it }

        // Priority 4: Block player's fork
        findForkingMove(board, CellState.X, availableMoves)?.let { return it }

        // Priority 5: Take center
        if (availableMoves.contains(GameConstants.CENTER)) {
            return GameConstants.CENTER
        }

        // Priority 6: Take strategic corner (opposite to player)
        getStrategicCorner(board, availableMoves)?.let { return it }

        // Priority 7: Random available move
        return availableMoves.random()
    }

    fun findWinningMove(board: List<CellState>, symbol: CellState): Int? {
        for (combo in GameConstants.WINNING_COMBINATIONS) {
            val cells = combo.map { board[it] }
            val symbolCount = cells.count { it == symbol }
            val emptyCount = cells.count { it == CellState.EMPTY }

            if (symbolCount == 2 && emptyCount == 1) {
                val emptyIndex = combo.first { board[it] == CellState.EMPTY }
                return emptyIndex
            }
        }
        return null
    }

    private fun findForkingMove(
        board: List<CellState>,
        symbol: CellState,
        availableMoves: List<Int>
    ): Int? {
        for (move in availableMoves) {
            val testBoard = board.toMutableList()
            testBoard[move] = symbol

            var winningPaths = 0
            for (combo in GameConstants.WINNING_COMBINATIONS) {
                val cells = combo.map { testBoard[it] }
                val symbolCount = cells.count { it == symbol }
                val emptyCount = cells.count { it == CellState.EMPTY }

                if (symbolCount == 2 && emptyCount == 1) {
                    winningPaths++
                }
            }

            if (winningPaths >= 2) {
                return move
            }
        }
        return null
    }

    private fun getStrategicCorner(board: List<CellState>, availableMoves: List<Int>): Int? {
        val availableCorners = GameConstants.CORNERS.filter { availableMoves.contains(it) }
        if (availableCorners.isEmpty()) return null

        // Prefer opposite corner to player
        for (corner in availableCorners) {
            val oppositeCorner = getOppositeCorner(corner)
            if (board[oppositeCorner] == CellState.X) {
                return corner
            }
        }

        return availableCorners.firstOrNull()
    }

    private fun getOppositeCorner(corner: Int): Int {
        return when (corner) {
            0 -> 8
            2 -> 6
            6 -> 2
            8 -> 0
            else -> corner
        }
    }
}
```

**Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "com.example.infinite_xox.game.AIStrategyTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: implement AI strategy with 7-tier decision tree"
```

---

## Task 5: Implement Game ViewModel

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/viewmodel/GameViewModel.kt`
- Create: `app/src/test/java/com/example/infinite_xox/viewmodel/GameViewModelTest.kt`

**Step 1: Write failing tests for GameViewModel**

```kotlin
package com.example.infinite_xox.viewmodel

import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameViewModelTest {

    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        viewModel = GameViewModel()
    }

    @Test
    fun `initial state should have empty board and player turn`() {
        val state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellState.EMPTY })
        assertTrue(state.isPlayerTurn)
        assertEquals(0, state.playerScore)
        assertEquals(0, state.aiScore)
    }

    @Test
    fun `player move should update board`() {
        viewModel.onCellClick(4)
        val state = viewModel.gameState.value
        assertEquals(CellState.X, state.board[4])
    }

    @Test
    fun `player cannot click on occupied cell`() {
        viewModel.onCellClick(4)
        val boardAfterFirstClick = viewModel.gameState.value.board.toList()

        viewModel.onCellClick(4) // Try to click same cell
        val boardAfterSecondClick = viewModel.gameState.value.board

        assertEquals(boardAfterFirstClick, boardAfterSecondClick)
    }

    @Test
    fun `resetRound should clear board but keep scores`() {
        viewModel.onCellClick(0)
        viewModel.resetRound()

        val state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellState.EMPTY })
        assertTrue(state.moveHistory.isEmpty())
    }

    @Test
    fun `newGame should reset everything`() {
        viewModel.onCellClick(0)
        viewModel.newGame()

        val state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellState.EMPTY })
        assertEquals(0, state.playerScore)
        assertEquals(0, state.aiScore)
        assertEquals(0, state.roundCount)
    }
}
```

**Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "com.example.infinite_xox.viewmodel.GameViewModelTest"`
Expected: FAIL

**Step 3: Implement GameViewModel**

```kotlin
package com.example.infinite_xox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infinite_xox.game.AIStrategy
import com.example.infinite_xox.game.GameLogic
import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameConstants
import com.example.infinite_xox.model.GameState
import com.example.infinite_xox.model.Move
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val gameLogic = GameLogic()
    private val aiStrategy = AIStrategy()

    fun onCellClick(index: Int) {
        val currentState = _gameState.value

        if (currentState.isGameOver ||
            currentState.isMatchOver ||
            !currentState.isPlayerTurn ||
            currentState.isProcessingMove ||
            currentState.board[index] != CellState.EMPTY
        ) {
            return
        }

        makePlayerMove(index)
    }

    private fun makePlayerMove(index: Int) {
        val currentState = _gameState.value
        val playerPieceCount = gameLogic.countSymbols(currentState.board, CellState.X)

        if (playerPieceCount >= GameConstants.MAX_PIECES) {
            moveOldestPiece(index, CellState.X)
        } else {
            makeMove(index, CellState.X)
        }
    }

    private fun makeMove(index: Int, symbol: CellState) {
        _gameState.update { it.copy(isProcessingMove = true) }

        val currentState = _gameState.value
        val newBoard = currentState.board.toMutableList()
        newBoard[index] = symbol

        val newMove = Move(index = index, symbol = symbol)
        val newHistory = currentState.moveHistory + newMove

        _gameState.update {
            it.copy(
                board = newBoard,
                moveHistory = newHistory
            )
        }

        val winningCombo = gameLogic.checkWinner(newBoard, symbol)
        if (winningCombo != null) {
            handleRoundWin(symbol, winningCombo)
            return
        }

        if (gameLogic.isDraw(newBoard, newHistory)) {
            handleDraw()
            return
        }

        val isPlayerTurn = symbol == CellState.O
        val statusMessage = if (isPlayerTurn) "Your Turn (X)" else "AI's Turn (O)"

        _gameState.update {
            it.copy(
                isPlayerTurn = isPlayerTurn,
                statusMessage = statusMessage,
                isProcessingMove = false
            )
        }

        if (!isPlayerTurn) {
            scheduleAIMove()
        }
    }

    private fun moveOldestPiece(toIndex: Int, symbol: CellState) {
        val currentState = _gameState.value
        val oldestIndex = gameLogic.getOldestMoveIndex(currentState.moveHistory, symbol)
            ?: return

        val newBoard = currentState.board.toMutableList()
        newBoard[oldestIndex] = CellState.EMPTY
        newBoard[toIndex] = symbol

        val newHistory = currentState.moveHistory
            .filter { it.index != oldestIndex || it.symbol != symbol }
            .plus(Move(index = toIndex, symbol = symbol))

        _gameState.update {
            it.copy(
                board = newBoard,
                moveHistory = newHistory,
                isProcessingMove = true
            )
        }

        val winningCombo = gameLogic.checkWinner(newBoard, symbol)
        if (winningCombo != null) {
            handleRoundWin(symbol, winningCombo)
            return
        }

        val isPlayerTurn = symbol == CellState.O
        val statusMessage = if (isPlayerTurn) "Your Turn (X)" else "AI's Turn (O)"

        _gameState.update {
            it.copy(
                isPlayerTurn = isPlayerTurn,
                statusMessage = statusMessage,
                isProcessingMove = false
            )
        }

        if (!isPlayerTurn) {
            scheduleAIMove()
        }
    }

    private fun scheduleAIMove() {
        viewModelScope.launch {
            delay(GameConstants.AI_MOVE_DELAY_MS)
            makeAIMove()
        }
    }

    private fun makeAIMove() {
        val currentState = _gameState.value
        if (currentState.isGameOver || currentState.isMatchOver || currentState.isPlayerTurn) {
            return
        }

        val aiPieceCount = gameLogic.countSymbols(currentState.board, CellState.O)
        val availableMoves = gameLogic.getAvailableMoves(currentState.board)

        if (availableMoves.isEmpty()) return

        val bestMove = aiStrategy.findBestMove(currentState.board, availableMoves)

        if (aiPieceCount >= GameConstants.MAX_PIECES) {
            moveOldestPiece(bestMove, CellState.O)
        } else {
            makeMove(bestMove, CellState.O)
        }
    }

    private fun handleRoundWin(symbol: CellState, winningCombo: List<Int>) {
        val currentState = _gameState.value
        val isPlayerWin = symbol == CellState.X

        val newPlayerScore = if (isPlayerWin) currentState.playerScore + 1 else currentState.playerScore
        val newAiScore = if (!isPlayerWin) currentState.aiScore + 1 else currentState.aiScore

        val isMatchOver = newPlayerScore >= GameConstants.ROUNDS_TO_WIN ||
                newAiScore >= GameConstants.ROUNDS_TO_WIN

        val statusMessage = when {
            isMatchOver && isPlayerWin -> "You Win the Match!"
            isMatchOver -> "AI Wins the Match!"
            isPlayerWin -> "You Win this Round!"
            else -> "AI Wins this Round!"
        }

        _gameState.update {
            it.copy(
                playerScore = newPlayerScore,
                aiScore = newAiScore,
                isGameOver = true,
                isMatchOver = isMatchOver,
                winningCombination = winningCombo,
                statusMessage = statusMessage,
                isProcessingMove = false
            )
        }
    }

    private fun handleDraw() {
        _gameState.update {
            it.copy(
                isGameOver = true,
                statusMessage = "It's a Draw!",
                isProcessingMove = false
            )
        }
    }

    fun resetRound() {
        val currentState = _gameState.value
        val playerStarts = !currentState.playerStartedLastRound

        _gameState.update {
            GameState(
                playerScore = it.playerScore,
                aiScore = it.aiScore,
                roundCount = it.roundCount + 1,
                playerStartedLastRound = playerStarts,
                isPlayerTurn = playerStarts,
                statusMessage = if (playerStarts) "Your Turn (X)" else "AI's Turn (O)"
            )
        }

        if (!playerStarts) {
            scheduleAIMove()
        }
    }

    fun newGame() {
        _gameState.value = GameState()
    }
}
```

**Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "com.example.infinite_xox.viewmodel.GameViewModelTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: implement GameViewModel with game state management"
```

---

## Task 6: Create Theme and Colors

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/theme/Color.kt`
- Create: `app/src/main/java/com/example/infinite_xox/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/example/infinite_xox/ui/theme/Type.kt`

**Step 1: Create Color.kt**

```kotlin
package com.example.infinite_xox.ui.theme

import androidx.compose.ui.graphics.Color

// Primary colors (Player X - Indigo)
val PrimaryColor = Color(0xFF6366F1)
val PrimaryDark = Color(0xFF4F46E5)
val PrimaryLight = Color(0xFF818CF8)

// Secondary colors (Slate)
val SecondaryColor = Color(0xFF64748B)

// Success color (Green - winning line)
val SuccessColor = Color(0xFF10B981)

// Danger color (Red - AI O)
val DangerColor = Color(0xFFEF4444)

// Background colors (Dark navy)
val BgPrimary = Color(0xFF0F172A)
val BgSecondary = Color(0xFF1E293B)
val BgTertiary = Color(0xFF334155)

// Text colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFFCBD5E1)
val TextMuted = Color(0xFF64748B)

// Cell colors
val CellBackground = Color(0xFF1E293B)
val CellBorder = Color(0xFF475569)
val CellHover = Color(0xFF334155)
```

**Step 2: Create Type.kt**

```kotlin
package com.example.infinite_xox.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)
```

**Step 3: Create Theme.kt**

```kotlin
package com.example.infinite_xox.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = TextPrimary,
    secondary = SecondaryColor,
    onSecondary = TextPrimary,
    tertiary = SuccessColor,
    background = BgPrimary,
    onBackground = TextPrimary,
    surface = BgSecondary,
    onSurface = TextPrimary,
    error = DangerColor,
    onError = TextPrimary
)

@Composable
fun InfiniteXoxTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BgPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

**Step 4: Commit**

```bash
git add .
git commit -m "feat: add app theme with dark color scheme"
```

---

## Task 7: Create Game Board Composable

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/components/GameBoard.kt`

**Step 1: Implement GameBoard composable**

```kotlin
package com.example.infinite_xox.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.ui.theme.BgTertiary
import com.example.infinite_xox.ui.theme.CellBackground
import com.example.infinite_xox.ui.theme.CellBorder
import com.example.infinite_xox.ui.theme.DangerColor
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.SuccessColor

@Composable
fun GameBoard(
    board: List<CellState>,
    winningCombination: List<Int>?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in 0..2) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    val isWinningCell = winningCombination?.contains(index) == true

                    GameCell(
                        cellState = board[index],
                        isWinningCell = isWinningCell,
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun GameCell(
    cellState: CellState,
    isWinningCell: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isWinningCell -> SuccessColor.copy(alpha = 0.3f)
            cellState != CellState.EMPTY -> BgTertiary
            else -> CellBackground
        },
        animationSpec = tween(durationMillis = 200),
        label = "cellBgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isWinningCell -> SuccessColor
            cellState == CellState.X -> PrimaryColor
            cellState == CellState.O -> DangerColor
            else -> CellBorder
        },
        animationSpec = tween(durationMillis = 200),
        label = "cellBorderColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (cellState != CellState.EMPTY) 1f else 0.95f,
        animationSpec = tween(durationMillis = 150),
        label = "cellScale"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = cellState == CellState.EMPTY) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cellState != CellState.EMPTY) {
            Text(
                text = cellState.symbol,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (cellState == CellState.X) PrimaryColor else DangerColor
            )
        }
    }
}
```

**Step 2: Commit**

```bash
git add .
git commit -m "feat: add GameBoard composable with cell animations"
```

---

## Task 8: Create Score Board Composable

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/components/ScoreBoard.kt`

**Step 1: Implement ScoreBoard composable**

```kotlin
package com.example.infinite_xox.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.BgTertiary
import com.example.infinite_xox.ui.theme.DangerColor
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.TextMuted
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun ScoreBoard(
    playerScore: Int,
    aiScore: Int,
    isPlayerTurn: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BgSecondary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player score
        ScoreItem(
            label = "You",
            symbol = "X",
            score = playerScore,
            color = PrimaryColor,
            isActive = isPlayerTurn
        )

        // VS
        Text(
            text = "VS",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )

        // AI score
        ScoreItem(
            label = "AI",
            symbol = "O",
            score = aiScore,
            color = DangerColor,
            isActive = !isPlayerTurn
        )
    }
}

@Composable
private fun ScoreItem(
    label: String,
    symbol: String,
    score: Int,
    color: androidx.compose.ui.graphics.Color,
    isActive: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "scoreScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(if (isActive) color.copy(alpha = 0.2f) else BgTertiary)
                    .border(
                        width = 2.dp,
                        color = if (isActive) color else TextMuted,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }

        Text(
            text = score.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
```

**Step 2: Commit**

```bash
git add .
git commit -m "feat: add ScoreBoard composable with turn indicator"
```

---

## Task 9: Create Game Status and Pieces Counter

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/components/GameStatus.kt`
- Create: `app/src/main/java/com/example/infinite_xox/ui/components/PiecesCounter.kt`

**Step 1: Implement GameStatus composable**

```kotlin
package com.example.infinite_xox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.TextPrimary

@Composable
fun GameStatus(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BgSecondary)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
```

**Step 2: Implement PiecesCounter composable**

```kotlin
package com.example.infinite_xox.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameConstants
import com.example.infinite_xox.ui.theme.DangerColor
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun PiecesCounter(
    board: List<CellState>,
    modifier: Modifier = Modifier
) {
    val playerPieces = board.count { it == CellState.X }
    val aiPieces = board.count { it == CellState.O }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Your pieces: $playerPieces/${GameConstants.MAX_PIECES}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        )
        Text(
            text = "AI pieces: $aiPieces/${GameConstants.MAX_PIECES}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DangerColor
        )
    }
}
```

**Step 3: Commit**

```bash
git add .
git commit -m "feat: add GameStatus and PiecesCounter composables"
```

---

## Task 10: Create Game Control Buttons

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/components/GameControls.kt`

**Step 1: Implement GameControls composable**

```kotlin
package com.example.infinite_xox.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.ui.theme.BgTertiary
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun GameControls(
    onResetRound: () -> Unit,
    onNewGame: () -> Unit,
    isGameOver: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onResetRound,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextSecondary
            )
        ) {
            Text(
                text = if (isGameOver) "Next Round" else "Reset Round",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = onNewGame,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                contentColor = TextPrimary
            )
        ) {
            Text(
                text = "New Game",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
```

**Step 2: Commit**

```bash
git add .
git commit -m "feat: add GameControls composable with buttons"
```

---

## Task 11: Create Info Modal Dialog

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/components/InfoDialog.kt`

**Step 1: Implement InfoDialog composable**

```kotlin
package com.example.infinite_xox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.TextMuted
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun InfoDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(BgSecondary)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "How to Play",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Game Rules
            InfoSection(
                title = "The Infinite Twist",
                content = "Unlike regular Tic-Tac-Toe, each player can only have 3 pieces on the board. When you place your 4th piece, your oldest piece disappears!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoSection(
                title = "Winning",
                content = "Get 3 in a row (horizontal, vertical, or diagonal) to win a round. Win 3 rounds to win the match!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoSection(
                title = "Strategy Tips",
                items = listOf(
                    "Track your oldest piece - it will move next",
                    "Plan moves ahead considering piece rotation",
                    "Control the center for flexibility",
                    "Watch the AI's oldest piece too"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoSection(
                title = "Fair Play",
                content = "First move alternates each round, ensuring balanced gameplay."
            )
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: String? = null,
    items: List<String>? = null
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        content?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }

        items?.forEach { item ->
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "•",
                    fontSize = 14.sp,
                    color = PrimaryColor
                )
                Text(
                    text = item,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
```

**Step 2: Commit**

```bash
git add .
git commit -m "feat: add InfoDialog with game rules"
```

---

## Task 12: Create Game Over Modal Dialog

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/components/GameOverDialog.kt`

**Step 1: Implement GameOverDialog composable**

```kotlin
package com.example.infinite_xox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.DangerColor
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.SuccessColor
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun GameOverDialog(
    isPlayerWinner: Boolean,
    playerScore: Int,
    aiScore: Int,
    onPlayAgain: () -> Unit
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(BgSecondary)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Trophy/Result emoji
            Text(
                text = if (isPlayerWinner) "🏆" else "🤖",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Result message
            Text(
                text = if (isPlayerWinner) "You Won!" else "AI Won!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPlayerWinner) SuccessColor else DangerColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Final score
            Text(
                text = "Final Score: $playerScore - $aiScore",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isPlayerWinner)
                    "Congratulations! You've defeated the AI!"
                else
                    "The AI was too smart this time. Try again!",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = TextPrimary
                )
            ) {
                Text(
                    text = "Play Again",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
```

**Step 2: Commit**

```bash
git add .
git commit -m "feat: add GameOverDialog for match completion"
```

---

## Task 13: Create Main Game Screen

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/screens/GameScreen.kt`

**Step 1: Implement GameScreen composable**

```kotlin
package com.example.infinite_xox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameConstants
import com.example.infinite_xox.ui.components.GameBoard
import com.example.infinite_xox.ui.components.GameControls
import com.example.infinite_xox.ui.components.GameOverDialog
import com.example.infinite_xox.ui.components.GameStatus
import com.example.infinite_xox.ui.components.InfoDialog
import com.example.infinite_xox.ui.components.PiecesCounter
import com.example.infinite_xox.ui.components.ScoreBoard
import com.example.infinite_xox.ui.theme.BgPrimary
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.TextMuted
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary
import com.example.infinite_xox.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Infinite",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Tic-Tac-Toe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Score board
                ScoreBoard(
                    playerScore = gameState.playerScore,
                    aiScore = gameState.aiScore,
                    isPlayerTurn = gameState.isPlayerTurn
                )
            }

            // Game section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status message
                GameStatus(message = gameState.statusMessage)

                Spacer(modifier = Modifier.height(20.dp))

                // Game board
                GameBoard(
                    board = gameState.board,
                    winningCombination = gameState.winningCombination,
                    onCellClick = { index -> viewModel.onCellClick(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pieces counter
                PiecesCounter(board = gameState.board)
            }

            // Controls section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Game controls
                GameControls(
                    onResetRound = { viewModel.resetRound() },
                    onNewGame = { viewModel.newGame() },
                    isGameOver = gameState.isGameOver
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info button
                IconButton(
                    onClick = { showInfoDialog = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BgSecondary)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "How to Play",
                        tint = TextMuted
                    )
                }
            }
        }

        // Dialogs
        if (showInfoDialog) {
            InfoDialog(onDismiss = { showInfoDialog = false })
        }

        if (gameState.isMatchOver) {
            GameOverDialog(
                isPlayerWinner = gameState.playerScore >= GameConstants.ROUNDS_TO_WIN,
                playerScore = gameState.playerScore,
                aiScore = gameState.aiScore,
                onPlayAgain = { viewModel.newGame() }
            )
        }
    }
}
```

**Step 2: Commit**

```bash
git add .
git commit -m "feat: add GameScreen composable combining all UI components"
```

---

## Task 14: Create MainActivity

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/MainActivity.kt`
- Modify: `app/src/main/AndroidManifest.xml`

**Step 1: Implement MainActivity**

```kotlin
package com.example.infinite_xox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.infinite_xox.ui.screens.GameScreen
import com.example.infinite_xox.ui.theme.BgPrimary
import com.example.infinite_xox.ui.theme.InfiniteXoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InfiniteXoxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgPrimary
                ) {
                    GameScreen()
                }
            }
        }
    }
}
```

**Step 2: Update AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Infinitexox"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.Infinitexox">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

**Step 3: Run and verify the app builds and launches**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add .
git commit -m "feat: add MainActivity and configure app launch"
```

---

## Task 15: Final Testing and Polish

**Files:**
- All test files

**Step 1: Run all unit tests**

Run: `./gradlew test`
Expected: All tests pass

**Step 2: Build release APK**

Run: `./gradlew assembleRelease`
Expected: BUILD SUCCESSFUL

**Step 3: Final commit**

```bash
git add .
git commit -m "chore: final testing and verification complete"
```

---

## Summary

| Task | Description | Files |
|------|-------------|-------|
| 1 | Setup Compose dependencies | build.gradle.kts, libs.versions.toml |
| 2 | Data models | GameModels.kt |
| 3 | Win detection logic | GameLogic.kt |
| 4 | AI strategy | AIStrategy.kt |
| 5 | Game ViewModel | GameViewModel.kt |
| 6 | Theme and colors | Color.kt, Theme.kt, Type.kt |
| 7 | Game board UI | GameBoard.kt |
| 8 | Score board UI | ScoreBoard.kt |
| 9 | Status & counter UI | GameStatus.kt, PiecesCounter.kt |
| 10 | Control buttons | GameControls.kt |
| 11 | Info dialog | InfoDialog.kt |
| 12 | Game over dialog | GameOverDialog.kt |
| 13 | Main game screen | GameScreen.kt |
| 14 | MainActivity | MainActivity.kt, AndroidManifest.xml |
| 15 | Final testing | All tests |

Total: 15 tasks with TDD approach and frequent commits.
