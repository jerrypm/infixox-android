package com.example.infinite_xox.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var velocityX: Float,
    var velocityY: Float,
    var rotation: Float,
    var rotationSpeed: Float,
    var size: Float,
    var color: Color,
    var alpha: Float = 1f
)

fun createConfettiParticles(
    count: Int,
    screenWidth: Float,
    colors: List<Color>
): List<ConfettiParticle> {
    return List(count) {
        ConfettiParticle(
            x = Random.nextFloat() * screenWidth,
            y = -Random.nextFloat() * 200f - 50f,
            velocityX = Random.nextFloat() * 6f - 3f,
            velocityY = Random.nextFloat() * 4f + 2f,
            rotation = Random.nextFloat() * 360f,
            rotationSpeed = Random.nextFloat() * 10f - 5f,
            size = Random.nextFloat() * 12f + 6f,
            color = colors[Random.nextInt(colors.size)]
        )
    }
}

@Composable
fun ConfettiAnimation(
    isPlayerWinner: Boolean,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    val colors = if (isPlayerWinner) {
        listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFFA500), // Orange
            Color(0xFF00FF00), // Green
            Color(0xFF00FFFF), // Cyan
            Color(0xFFFF69B4)  // Pink
        )
    } else {
        listOf(
            Color(0xFFFF4444), // Red
            Color(0xFFFF6B6B), // Light Red
            Color(0xFFCC3333), // Dark Red
            Color(0xFFFFAAAA), // Pink
            Color(0xFFFF8888)  // Salmon
        )
    }

    var particles by remember {
        mutableStateOf(createConfettiParticles(150, screenWidth, colors))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing)
        ),
        label = "confetti_progress"
    )

    LaunchedEffect(animationProgress) {
        particles = particles.map { particle ->
            particle.copy(
                x = particle.x + particle.velocityX,
                y = particle.y + particle.velocityY,
                rotation = particle.rotation + particle.rotationSpeed,
                velocityY = particle.velocityY + 0.1f, // gravity
                alpha = if (particle.y > screenHeight) 0f else particle.alpha
            )
        }

        // Respawn particles that have fallen off screen
        val activeCount = particles.count { it.alpha > 0f }
        if (activeCount < 50) {
            particles = particles + createConfettiParticles(50, screenWidth, colors)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.filter { it.alpha > 0f }.forEach { particle ->
            rotate(particle.rotation, pivot = Offset(particle.x, particle.y)) {
                drawRect(
                    color = particle.color.copy(alpha = particle.alpha),
                    topLeft = Offset(particle.x - particle.size / 2, particle.y - particle.size / 2),
                    size = Size(particle.size, particle.size * 0.6f)
                )
            }
        }
    }
}
