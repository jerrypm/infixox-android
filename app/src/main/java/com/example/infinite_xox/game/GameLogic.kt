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
        // Return the first matching move (oldest, since moves are added in order)
        return moveHistory
            .firstOrNull { it.symbol == symbol }
            ?.index
    }

    fun isDraw(board: List<CellState>, moveHistory: List<Move>): Boolean {
        // In infinite tic-tac-toe, draws are extremely rare
        // Since pieces can be moved, we should almost never declare a draw
        // Matching JavaScript logic: always return false to prevent unwanted resets
        return false
    }
}
