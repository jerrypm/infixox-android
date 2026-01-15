package com.example.infinite_xox.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun GameControls(
    onResetRound: () -> Unit,
    onNewGame: () -> Unit,
    isGameOver: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onResetRound,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextSecondary
            )
        ) {
            Text(
                text = if (isGameOver) "Next Round" else "Reset Round",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = onNewGame,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                contentColor = TextPrimary
            )
        ) {
            Text(
                text = "New Game",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
