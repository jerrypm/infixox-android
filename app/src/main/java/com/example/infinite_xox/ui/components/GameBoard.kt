package com.example.infinite_xox.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.model.CellState
import com.example.infinite_xox.ui.theme.BgTertiary
import com.example.infinite_xox.ui.theme.CellBackground
import com.example.infinite_xox.ui.theme.CellBorder
import com.example.infinite_xox.ui.theme.DangerColor
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.SuccessColor

@Composable
fun GameBoard(
    board: List<CellState>,
    winningCombination: List<Int>?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in 0..2) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    val isWinningCell = winningCombination?.contains(index) == true

                    GameCell(
                        cellState = board[index],
                        isWinningCell = isWinningCell,
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun GameCell(
    cellState: CellState,
    isWinningCell: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isWinningCell -> SuccessColor.copy(alpha = 0.3f)
            cellState != CellState.EMPTY -> BgTertiary
            else -> CellBackground
        },
        animationSpec = tween(durationMillis = 200),
        label = "cellBgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isWinningCell -> SuccessColor
            cellState == CellState.X -> PrimaryColor
            cellState == CellState.O -> DangerColor
            else -> CellBorder
        },
        animationSpec = tween(durationMillis = 200),
        label = "cellBorderColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (cellState != CellState.EMPTY) 1f else 0.95f,
        animationSpec = tween(durationMillis = 150),
        label = "cellScale"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = cellState == CellState.EMPTY) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cellState != CellState.EMPTY) {
            Text(
                text = cellState.symbol,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (cellState == CellState.X) PrimaryColor else DangerColor
            )
        }
    }
}
