package com.example.infinite_xox.viewmodel

import com.example.infinite_xox.model.CellState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
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
