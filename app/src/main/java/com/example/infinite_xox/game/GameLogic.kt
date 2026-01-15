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
