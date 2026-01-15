package com.example.infinite_xox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.TextPrimary

@Composable
fun GameStatus(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BgSecondary)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
