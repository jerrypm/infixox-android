# Confetti Animation for Match Winner Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add confetti/fireworks animation when player wins the match (first to win 3 rounds).

**Architecture:** Create a custom Confetti composable using Canvas API that renders falling/exploding particles. Trigger animation only when match is over (not per round). Integrate with existing GameOverDialog.

**Tech Stack:** Jetpack Compose Canvas, Compose Animation APIs, Kotlin Coroutines

---

## Analysis

The game already has best-of-3 logic implemented:
- `GameConstants.ROUNDS_TO_WIN = 3`
- `isMatchOver` flag in GameState triggers when either player reaches 3 wins
- `GameOverDialog` shows when `isMatchOver` is true

What needs to be added:
1. Confetti animation component that shows particles/fireworks effect
2. Trigger animation when match is won (when `isMatchOver` becomes true)
3. Different animation color for player win vs AI win

---

### Task 1: Create Confetti Particle Data Model

**Files:**
- Create: `app/src/main/java/com/example/infinite_xox/ui/components/Confetti.kt`

**Step 1: Create the Confetti.kt file with particle data class**

```kotlin
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
```

**Step 2: Run build to verify no syntax errors**

Run: `./gradlew compileDebugKotlin`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add app/src/main/java/com/example/infinite_xox/ui/components/Confetti.kt
git commit -m "feat: add confetti particle data model"
```

---

### Task 2: Create Confetti Animation Composable

**Files:**
- Modify: `app/src/main/java/com/example/infinite_xox/ui/components/Confetti.kt`

**Step 1: Add the ConfettiAnimation composable**

Add the following after the existing code in Confetti.kt:

```kotlin
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay

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
```

**Step 2: Add missing import for dp**

Make sure this import is at the top of the file:
```kotlin
import androidx.compose.ui.unit.dp
```

**Step 3: Run build to verify**

Run: `./gradlew compileDebugKotlin`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add app/src/main/java/com/example/infinite_xox/ui/components/Confetti.kt
git commit -m "feat: add confetti animation composable with canvas rendering"
```

---

### Task 3: Integrate Confetti into GameOverDialog

**Files:**
- Modify: `app/src/main/java/com/example/infinite_xox/ui/components/GameOverDialog.kt`

**Step 1: Update GameOverDialog to include confetti**

Replace the entire GameOverDialog.kt content with:

```kotlin
package com.example.infinite_xox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.infinite_xox.ui.theme.BgSecondary
import com.example.infinite_xox.ui.theme.DangerColor
import com.example.infinite_xox.ui.theme.PrimaryColor
import com.example.infinite_xox.ui.theme.SuccessColor
import com.example.infinite_xox.ui.theme.TextPrimary
import com.example.infinite_xox.ui.theme.TextSecondary

@Composable
fun GameOverDialog(
    isPlayerWinner: Boolean,
    playerScore: Int,
    aiScore: Int,
    onPlayAgain: () -> Unit
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Confetti animation in background
            ConfettiAnimation(
                isPlayerWinner = isPlayerWinner,
                modifier = Modifier.fillMaxSize()
            )

            // Dialog content
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BgSecondary)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isPlayerWinner) "🏆" else "🤖",
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isPlayerWinner) "You Won!" else "AI Won!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlayerWinner) SuccessColor else DangerColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Final Score: $playerScore - $aiScore",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isPlayerWinner)
                        "Congratulations! You've defeated the AI!"
                    else
                        "The AI was too smart this time. Try again!",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = TextPrimary
                    )
                ) {
                    Text(
                        text = "Play Again",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
```

**Step 2: Run build to verify integration**

Run: `./gradlew compileDebugKotlin`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add app/src/main/java/com/example/infinite_xox/ui/components/GameOverDialog.kt
git commit -m "feat: integrate confetti animation into game over dialog"
```

---

### Task 4: Build and Test the Complete Feature

**Files:**
- None (testing only)

**Step 1: Run full build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 2: Run tests**

Run: `./gradlew test`
Expected: BUILD SUCCESSFUL with all tests passing

**Step 3: Final commit with feature complete**

```bash
git add -A
git commit -m "feat: complete confetti animation for match winner

- Added ConfettiParticle data model
- Created ConfettiAnimation composable with Canvas rendering
- Integrated confetti into GameOverDialog
- Different colors for player win (gold/green) vs AI win (red)"
```

---

## Summary

The implementation adds:
1. **ConfettiParticle** - Data class representing each confetti piece with position, velocity, rotation, color
2. **ConfettiAnimation** - Canvas-based animation that renders falling confetti particles
3. **Updated GameOverDialog** - Now shows confetti animation behind the dialog when match ends

The confetti colors are:
- **Player wins**: Gold, Orange, Green, Cyan, Pink (celebratory)
- **AI wins**: Various reds/pinks (less celebratory but still animated)

The animation:
- Creates 150 particles initially
- Particles fall with gravity effect
- Particles rotate as they fall
- New particles spawn when active count drops below 50
- Renders at ~20fps (50ms per frame)
