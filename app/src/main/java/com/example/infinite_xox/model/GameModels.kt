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
