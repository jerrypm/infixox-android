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
