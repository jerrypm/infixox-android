package com.example.infinite_xox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.TextMuted
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun InfoDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(BgSecondary)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "How to Play",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            InfoSection(
                title = "The Infinite Twist",
                content = "Unlike regular Tic-Tac-Toe, each player can only have 3 pieces on the board. When you place your 4th piece, your oldest piece disappears!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoSection(
                title = "Winning",
                content = "Get 3 in a row (horizontal, vertical, or diagonal) to win a round. Win 3 rounds to win the match!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoSection(
                title = "Strategy Tips",
                items = listOf(
                    "Track your oldest piece - it will move next",
                    "Plan moves ahead considering piece rotation",
                    "Control the center for flexibility",
                    "Watch the AI's oldest piece too"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoSection(
                title = "Fair Play",
                content = "First move alternates each round, ensuring balanced gameplay."
            )
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: String? = null,
    items: List<String>? = null
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        content?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }

        items?.forEach { item ->
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "•",
                    fontSize = 14.sp,
                    color = PrimaryColor
                )
                Text(
                    text = item,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
