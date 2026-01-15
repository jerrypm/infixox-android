package com.example.infinite_xox.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.BgTertiary
import com.example.infinite_xox.ui.theme.DangerColor
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.TextMuted
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun ScoreBoard(
    playerScore: Int,
    aiScore: Int,
    isPlayerTurn: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BgSecondary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreItem(
            label = "You",
            symbol = "X",
            score = playerScore,
            color = PrimaryColor,
            isActive = isPlayerTurn
        )

        Text(
            text = "VS",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )

        ScoreItem(
            label = "AI",
            symbol = "O",
            score = aiScore,
            color = DangerColor,
            isActive = !isPlayerTurn
        )
    }
}

@Composable
private fun ScoreItem(
    label: String,
    symbol: String,
    score: Int,
    color: androidx.compose.ui.graphics.Color,
    isActive: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "scoreScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(if (isActive) color.copy(alpha = 0.2f) else BgTertiary)
                    .border(
                        width = 2.dp,
                        color = if (isActive) color else TextMuted,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }

        Text(
            text = score.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
