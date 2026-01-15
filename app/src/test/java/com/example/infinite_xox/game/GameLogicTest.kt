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
