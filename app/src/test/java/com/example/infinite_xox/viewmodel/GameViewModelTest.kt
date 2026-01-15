package com.example.infinite_xox.viewmodel

import com.example.infinite_xox.model.CellState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private lateinit var viewModel: GameViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GameViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty board and player turn`() = runTest {
        val state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellState.EMPTY })
        assertTrue(state.isPlayerTurn)
        assertEquals(0, state.playerScore)
        assertEquals(0, state.aiScore)
    }

    @Test
    fun `player move should update board`() = runTest {
        viewModel.onCellClick(4)
        advanceUntilIdle()
        val state = viewModel.gameState.value
        assertEquals(CellState.X, state.board[4])
    }

    @Test
    fun `player cannot click on occupied cell`() = runTest {
        viewModel.onCellClick(4)
        advanceUntilIdle()
        val boardAfterFirstClick = viewModel.gameState.value.board.toList()

        viewModel.onCellClick(4) // Try to click same cell
        advanceUntilIdle()
        val boardAfterSecondClick = viewModel.gameState.value.board

        assertEquals(boardAfterFirstClick, boardAfterSecondClick)
    }

    @Test
    fun `resetRound should clear board but keep scores`() = runTest {
        viewModel.onCellClick(0)
        advanceUntilIdle()
        viewModel.resetRound()

        val state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellState.EMPTY })
        assertTrue(state.moveHistory.isEmpty())
    }

    @Test
    fun `newGame should reset everything`() = runTest {
        viewModel.onCellClick(0)
        advanceUntilIdle()
        viewModel.newGame()

        val state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellState.EMPTY })
        assertEquals(0, state.playerScore)
        assertEquals(0, state.aiScore)
        assertEquals(0, state.roundCount)
    }

    @Test
    fun `AI should make move after player move`() = runTest {
        viewModel.onCellClick(4) // Player moves to center
        advanceUntilIdle() // Wait for AI to move

        val state = viewModel.gameState.value
        val aiPieces = state.board.count { it == CellState.O }
        assertEquals(1, aiPieces) // AI should have made one move
        assertTrue(state.isPlayerTurn) // Should be player's turn again
    }
}
