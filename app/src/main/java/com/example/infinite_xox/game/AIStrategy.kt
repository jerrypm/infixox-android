package com.example.infinite_xox.game

import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameConstants

class AIStrategy {

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

        // Prioritize corners that are opposite to player's pieces
        val strategicCorners = mutableListOf<Int>()
        val playerCorners = GameConstants.CORNERS.filter { board[it] == CellState.X }

        for (corner in availableCorners) {
            val oppositeCorner = getOppositeCorner(corner)
            if (playerCorners.contains(oppositeCorner)) {
                strategicCorners.add(corner)
            }
        }

        // Return random from strategic corners if any, otherwise random from available corners
        return if (strategicCorners.isNotEmpty()) {
            strategicCorners.random()
        } else {
            availableCorners.random()
        }
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
