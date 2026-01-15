package com.example.infinite_xox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infinite_xox.game.AIStrategy
import com.example.infinite_xox.game.GameLogic
import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameConstants
import com.example.infinite_xox.model.GameState
import com.example.infinite_xox.model.Move
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val gameLogic = GameLogic()
    private val aiStrategy = AIStrategy()

    fun onCellClick(index: Int) {
        val currentState = _gameState.value

        if (currentState.isGameOver ||
            currentState.isMatchOver ||
            !currentState.isPlayerTurn ||
            currentState.isProcessingMove ||
            currentState.board[index] != CellState.EMPTY
        ) {
            return
        }

        makePlayerMove(index)
    }

    private fun makePlayerMove(index: Int) {
        val currentState = _gameState.value
        val playerPieceCount = gameLogic.countSymbols(currentState.board, CellState.X)

        if (playerPieceCount >= GameConstants.MAX_PIECES) {
            moveOldestPiece(index, CellState.X)
        } else {
            makeMove(index, CellState.X)
        }
    }

    private fun makeMove(index: Int, symbol: CellState) {
        _gameState.update { it.copy(isProcessingMove = true) }

        val currentState = _gameState.value
        val newBoard = currentState.board.toMutableList()
        newBoard[index] = symbol

        val newMove = Move(index = index, symbol = symbol)
        val newHistory = currentState.moveHistory + newMove

        _gameState.update {
            it.copy(
                board = newBoard,
                moveHistory = newHistory
            )
        }

        val winningCombo = gameLogic.checkWinner(newBoard, symbol)
        if (winningCombo != null) {
            handleRoundWin(symbol, winningCombo)
            return
        }

        if (gameLogic.isDraw(newBoard, newHistory)) {
            handleDraw()
            return
        }

        val isPlayerTurn = symbol == CellState.O
        val statusMessage = if (isPlayerTurn) "Your Turn (X)" else "AI's Turn (O)"

        _gameState.update {
            it.copy(
                isPlayerTurn = isPlayerTurn,
                statusMessage = statusMessage,
                isProcessingMove = false
            )
        }

        if (!isPlayerTurn) {
            scheduleAIMove()
        }
    }

    private fun moveOldestPiece(toIndex: Int, symbol: CellState) {
        val currentState = _gameState.value
        val oldestIndex = gameLogic.getOldestMoveIndex(currentState.moveHistory, symbol)
            ?: return

        val newBoard = currentState.board.toMutableList()
        newBoard[oldestIndex] = CellState.EMPTY
        newBoard[toIndex] = symbol

        val newHistory = currentState.moveHistory
            .filter { it.index != oldestIndex || it.symbol != symbol }
            .plus(Move(index = toIndex, symbol = symbol))

        _gameState.update {
            it.copy(
                board = newBoard,
                moveHistory = newHistory,
                isProcessingMove = true
            )
        }

        val winningCombo = gameLogic.checkWinner(newBoard, symbol)
        if (winningCombo != null) {
            handleRoundWin(symbol, winningCombo)
            return
        }

        val isPlayerTurn = symbol == CellState.O
        val statusMessage = if (isPlayerTurn) "Your Turn (X)" else "AI's Turn (O)"

        _gameState.update {
            it.copy(
                isPlayerTurn = isPlayerTurn,
                statusMessage = statusMessage,
                isProcessingMove = false
            )
        }

        if (!isPlayerTurn) {
            scheduleAIMove()
        }
    }

    private fun scheduleAIMove() {
        viewModelScope.launch {
            delay(GameConstants.AI_MOVE_DELAY_MS)
            makeAIMove()
        }
    }

    private fun makeAIMove() {
        val currentState = _gameState.value
        if (currentState.isGameOver || currentState.isMatchOver || currentState.isPlayerTurn) {
            return
        }

        val aiPieceCount = gameLogic.countSymbols(currentState.board, CellState.O)
        val availableMoves = gameLogic.getAvailableMoves(currentState.board)

        if (availableMoves.isEmpty()) return

        val bestMove = aiStrategy.findBestMove(currentState.board, availableMoves)

        if (aiPieceCount >= GameConstants.MAX_PIECES) {
            moveOldestPiece(bestMove, CellState.O)
        } else {
            makeMove(bestMove, CellState.O)
        }
    }

    private fun handleRoundWin(symbol: CellState, winningCombo: List<Int>) {
        val currentState = _gameState.value
        val isPlayerWin = symbol == CellState.X

        val newPlayerScore = if (isPlayerWin) currentState.playerScore + 1 else currentState.playerScore
        val newAiScore = if (!isPlayerWin) currentState.aiScore + 1 else currentState.aiScore

        val isMatchOver = newPlayerScore >= GameConstants.ROUNDS_TO_WIN ||
                newAiScore >= GameConstants.ROUNDS_TO_WIN

        val statusMessage = when {
            isMatchOver && isPlayerWin -> "You Win the Match!"
            isMatchOver -> "AI Wins the Match!"
            isPlayerWin -> "You Win this Round!"
            else -> "AI Wins this Round!"
        }

        _gameState.update {
            it.copy(
                playerScore = newPlayerScore,
                aiScore = newAiScore,
                isGameOver = true,
                isMatchOver = isMatchOver,
                winningCombination = winningCombo,
                statusMessage = statusMessage,
                isProcessingMove = false
            )
        }
    }

    private fun handleDraw() {
        _gameState.update {
            it.copy(
                isGameOver = true,
                statusMessage = "It's a Draw!",
                isProcessingMove = false
            )
        }
    }

    fun resetRound() {
        val currentState = _gameState.value
        val playerStarts = !currentState.playerStartedLastRound

        _gameState.update {
            GameState(
                playerScore = it.playerScore,
                aiScore = it.aiScore,
                roundCount = it.roundCount + 1,
                playerStartedLastRound = playerStarts,
                isPlayerTurn = playerStarts,
                statusMessage = if (playerStarts) "Your Turn (X)" else "AI's Turn (O)"
            )
        }

        if (!playerStarts) {
            scheduleAIMove()
        }
    }

    fun newGame() {
        _gameState.value = GameState()
    }
}
