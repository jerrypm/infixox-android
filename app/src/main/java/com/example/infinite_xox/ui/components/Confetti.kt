package com.example.infinite_xox.ui.components

import androidx.compose.ui.graphics.Color
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
