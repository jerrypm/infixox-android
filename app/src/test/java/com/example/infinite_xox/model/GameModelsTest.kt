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
