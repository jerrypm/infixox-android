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

    @Test
    fun `AI should create fork when possible`() {
        // Board where AI (O) can create a fork
        val board = listOf(
            CellState.X, CellState.EMPTY, CellState.O,
            CellState.EMPTY, CellState.X, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.O
        )
        val availableMoves = listOf(1, 3, 5, 6, 7)
        val move = aiStrategy.findBestMove(board, availableMoves)
        // AI should take position 5 or 6 to create a fork
        // Position 5: creates fork with row 3-4-5 and column 2-5-8
        // Position 6: creates fork with column 0-3-6 and diagonal 2-4-6
        assertTrue(move in listOf(5, 6))
    }

    @Test
    fun `AI should block player fork`() {
        // Board where player (X) could create a fork
        val board = listOf(
            CellState.X, CellState.EMPTY, CellState.EMPTY,
            CellState.EMPTY, CellState.O, CellState.EMPTY,
            CellState.EMPTY, CellState.EMPTY, CellState.X
        )
        val availableMoves = listOf(1, 2, 3, 5, 6, 7)
        val move = aiStrategy.findBestMove(board, availableMoves)
        // AI should block by taking a corner or edge to prevent fork
        assertTrue(move in listOf(1, 2, 3, 5, 6, 7))
    }
}
