package com.example.infinite_xox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.infinite_xox.model.GameConstants
import com.example.infinite_xox.ui.components.GameBoard
import com.example.infinite_xox.ui.components.GameControls
import com.example.infinite_xox.ui.components.GameOverDialog
import com.example.infinite_xox.ui.components.GameStatus
import com.example.infinite_xox.ui.components.InfoDialog
import com.example.infinite_xox.ui.components.PiecesCounter
import com.example.infinite_xox.ui.components.ScoreBoard
import com.example.infinite_xox.ui.theme.BgPrimary
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.TextMuted
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary
import com.example.infinite_xox.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Infinite",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Tic-Tac-Toe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                ScoreBoard(
                    playerScore = gameState.playerScore,
                    aiScore = gameState.aiScore,
                    isPlayerTurn = gameState.isPlayerTurn
                )
            }

            // Game section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameStatus(message = gameState.statusMessage)

                Spacer(modifier = Modifier.height(20.dp))

                GameBoard(
                    board = gameState.board,
                    winningCombination = gameState.winningCombination,
                    onCellClick = { index -> viewModel.onCellClick(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                PiecesCounter(board = gameState.board)
            }

            // Controls section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameControls(
                    onResetRound = { viewModel.resetRound() },
                    onNewGame = { viewModel.newGame() },
                    isGameOver = gameState.isGameOver
                )

                Spacer(modifier = Modifier.height(16.dp))

                IconButton(
                    onClick = { showInfoDialog = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BgSecondary)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "How to Play",
                        tint = TextMuted
                    )
                }
            }
        }

        // Dialogs
        if (showInfoDialog) {
            InfoDialog(onDismiss = { showInfoDialog = false })
        }

        if (gameState.isMatchOver) {
            GameOverDialog(
                isPlayerWinner = gameState.playerScore >= GameConstants.ROUNDS_TO_WIN,
                playerScore = gameState.playerScore,
                aiScore = gameState.aiScore,
                onPlayAgain = { viewModel.newGame() }
            )
        }
    }
}
