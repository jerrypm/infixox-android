package com.example.infinite_xox.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.model.GameConstants
import com.example.infinite_xox.ui.theme.DangerColor
import com.example.infinite_xox.ui.theme.PrimaryColor

@Composable
fun PiecesCounter(
    board: List<CellState>,
    modifier: Modifier = Modifier
) {
    val playerPieces = board.count { it == CellState.X }
    val aiPieces = board.count { it == CellState.O }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Your pieces: $playerPieces/${GameConstants.MAX_PIECES}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        )
        Text(
            text = "AI pieces: $aiPieces/${GameConstants.MAX_PIECES}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DangerColor
        )
    }
}
