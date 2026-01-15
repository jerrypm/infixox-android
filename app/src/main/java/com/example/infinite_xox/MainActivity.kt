package com.example.infinite_xox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.infinite_xox.ui.screens.GameScreen
import com.example.infinite_xox.ui.theme.BgPrimary
import com.example.infinite_xox.ui.theme.InfiniteXoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InfiniteXoxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgPrimary
                ) {
                    GameScreen()
                }
            }
        }
    }
}
